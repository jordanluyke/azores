package com.jordanluyke.azores.web.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jordanluyke.azores.Config;
import com.jordanluyke.azores.util.NodeUtil;
import com.jordanluyke.azores.web.model.*;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Singleton
@AllArgsConstructor(onConstructor = @__(@Inject))
public class ApiManagerImpl implements ApiManager {
    private static final Logger logger = LogManager.getLogger(ApiManager.class);

    private final ApiV1 apiV1 = new ApiV1();

    private Config config;

    @Override
    public Single<HttpServerResponse> handleRequest(HttpServerRequest req) {
        return Single.defer(() -> {
            if(!Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE).contains(req.getMethod()))
                throw new WebException(HttpResponseStatus.METHOD_NOT_ALLOWED);
            return Observable.fromIterable(apiV1.getHttpRoutes())
                    .filter(route -> {
                        if(req.getMethod() != route.getMethod())
                            return false;

                        String[] splitRequestPath = req.getPath().split("/");
                        String[] splitRouteHandlerPath = route.getPath().split("/");

                        if(splitRequestPath.length != splitRouteHandlerPath.length)
                            return false;

                        IntStream.range(0, splitRouteHandlerPath.length)
                                .filter(i -> splitRouteHandlerPath[i].startsWith(":"))
                                .forEach(i -> {
                                    splitRequestPath[i] = "*";
                                    splitRouteHandlerPath[i] = "*";
                                });

                        String joinedRequestPath = String.join("/", splitRequestPath);
                        String joinedRouteHandlerPath = String.join("/", splitRouteHandlerPath);

                        return joinedRequestPath.equals(joinedRouteHandlerPath);
                    })
                    .singleOrError()
                    .onErrorResumeWith(Single.error(new WebException(HttpResponseStatus.NOT_FOUND)));
        })
                .doOnSuccess(route -> {
                    List<String> splitRouteHandlerPath = Arrays.asList(route.getPath().split("/"));
                    List<String> splitRequestPath = Arrays.asList(req.getPath().split("/"));
                    List<Integer> paramIndexes = new ArrayList<>();
                    for(int i = 0; i < splitRouteHandlerPath.size(); i++)
                        if(splitRouteHandlerPath.get(i).startsWith(":"))
                            paramIndexes.add(i);
                    paramIndexes.forEach(i -> {
                        String name = splitRouteHandlerPath.get(i).substring(1);
                        String value = splitRequestPath.get(i);
                        req.getQueryParams().put(name, value);
                    });

                    if(req.getMethod() != HttpMethod.GET) {
                        if(req.getContent().length > 0) {
                            try {
                                if(req.getHeaders().get(HttpHeaderNames.CONTENT_TYPE.toString()).equalsIgnoreCase(HttpHeaderValues.APPLICATION_JSON.toString())) {
                                    req.setBody(Optional.of(NodeUtil.getJsonNode(req.getContent())));
                                } else if(req.getHeaders().get(HttpHeaderNames.CONTENT_TYPE.toString()).equalsIgnoreCase(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())) {
                                    String form = URLDecoder.decode(new String(req.getContent(), CharsetUtil.UTF_8), CharsetUtil.UTF_8);
                                    Map<String, String> params = Arrays.stream(form.split("&"))
                                            .map(entry -> Arrays.asList(entry.split("=")))
                                            .collect(Collectors.toMap(pair -> pair.get(0), pair -> pair.get(1)));
                                    req.setBody(Optional.of(NodeUtil.mapper.valueToTree(params)));
                                }
                            } catch(RuntimeException e) {
                                throw new WebException(HttpResponseStatus.BAD_REQUEST, "Unable to parse body");
                            }
                        }
                    }
                })
                .map(route -> config.getInjector().getInstance(route.getHandler()))
                .flatMap(instance -> instance.handle(req))
                .flatMap(resObject -> {
                    if(resObject instanceof HttpServerResponse)
                        return Single.just((HttpServerResponse) resObject);

                    HttpServerResponse res = new HttpServerResponse();
                    res.setStatus(HttpResponseStatus.OK);

                    if(resObject instanceof ObjectNode) {
                        res.setBody((ObjectNode) resObject);
                    } else {
                        try {
                            res.setBody(NodeUtil.mapper.valueToTree(resObject));
                        } catch(IllegalArgumentException e) {
                            logger.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
                            throw new WebException(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                        }
                    }

                    return Single.just(res);
                })
                .onErrorResumeNext(err -> {
                    WebException e = (err instanceof WebException) ? (WebException) err : new WebException(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    if(!(err instanceof WebException)) {
                        logger.error("Error: {}", err.getMessage());
                        Stream.of(err.getStackTrace())
                                .forEach(trace -> logger.error("{}", trace));
                    }
                    return Single.just(e.toHttpServerResponse());
                });
    }
}
