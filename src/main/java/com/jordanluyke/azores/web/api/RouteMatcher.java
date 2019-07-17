package com.jordanluyke.azores.web.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.jordanluyke.azores.Config;
import com.jordanluyke.azores.util.NodeUtil;
import com.jordanluyke.azores.web.api.model.HttpRoute;
import com.jordanluyke.azores.web.model.*;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;

import java.util.*;
import java.util.stream.IntStream;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class RouteMatcher {
    private static final Logger logger = LogManager.getLogger(RouteMatcher.class);

    private Config config;

    private ApiV1 apiV1 = new ApiV1();
    private List<HttpRoute> routes = apiV1.getHttpRoutes();

    @Inject
    public RouteMatcher(Config config) {
        this.config = config;
    }

    public Observable<HttpServerResponse> handle(HttpServerRequest request) {
        logger.info("{} {}", request.getMethod(), request.getPath());

        return Observable.just(request.getMethod())
                .flatMap(method -> {
                    if(!Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE).contains(method))
                        return Observable.error(new WebException(HttpResponseStatus.METHOD_NOT_ALLOWED));
                    return Observable.from(routes);
                })
                .filter(route -> request.getMethod() == route.getMethod())
                .filter(route -> {
                    String[] splitRequestPath = request.getPath().split("/");
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
                .take(1)
                .defaultIfEmpty(null)
                .flatMap(route -> {
                    if(route == null)
                        return Observable.error(new WebException(HttpResponseStatus.NOT_FOUND));

                    List<String> splitRouteHandlerPath = Arrays.asList(route.getPath().split("/"));
                    List<String> splitRequestPath = Arrays.asList(request.getPath().split("/"));

                    List<Integer> paramIndexes = new ArrayList<>();
                    for(int i = 0; i < splitRouteHandlerPath.size(); i++)
                        if(splitRouteHandlerPath.get(i).startsWith(":"))
                            paramIndexes.add(i);

                    Map<String, String> params = request.getQueryParams();
                    paramIndexes.forEach(i -> {
                        String name = splitRouteHandlerPath.get(i).substring(1);
                        String value = splitRequestPath.get(i);
                        params.put(name, value);
                    });
                    request.setQueryParams(params);

                    return Observable.just(route.getHandler());
                })
                .map(clazz -> config.getInjector().getInstance(clazz))
                .flatMap(instance -> instance.handle(Observable.just(request)))
                .flatMap(object -> {
                    if(object instanceof HttpServerResponse)
                        return Observable.just((HttpServerResponse) object);

                    HttpServerResponse res = new HttpServerResponse();
                    res.setStatus(HttpResponseStatus.OK);

                    if(object instanceof ObjectNode) {
                        res.setBody((ObjectNode) object);
                    } else {
                        try {
                            res.setBody(NodeUtil.mapper.valueToTree(object));
                        } catch(IllegalArgumentException e) {
                            logger.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
                            return Observable.error(new WebException(HttpResponseStatus.INTERNAL_SERVER_ERROR));
                        }
                    }

                    return Observable.just(res);
                });
    }
}
