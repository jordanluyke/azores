package com.jordanluyke.azores.web.netty;

import com.jordanluyke.azores.util.ErrorHandlingSubscriber;
import com.jordanluyke.azores.util.NodeUtil;
import com.jordanluyke.azores.web.api.ApiManager;
import com.jordanluyke.azores.web.model.HttpServerRequest;
import com.jordanluyke.azores.web.model.HttpServerResponse;
import com.jordanluyke.azores.web.model.WebException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class NettyHttpChannelInboundHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LogManager.getLogger(NettyHttpChannelInboundHandler.class);

    private ApiManager apiManager;

    private ByteBuf reqBuf = Unpooled.buffer();
    private HttpServerRequest httpServerRequest = new HttpServerRequest();

    public NettyHttpChannelInboundHandler(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            URI uri = new URI(httpRequest.uri());

            httpServerRequest.setPath(uri.getRawPath());
            httpServerRequest.setMethod(httpRequest.method());
            httpServerRequest.setHeaders(httpRequest.headers()
                    .entries()
                    .stream()
                    .collect(Collectors.toMap(key -> key.getKey().toLowerCase(), Map.Entry::getValue)));
            httpServerRequest.setQueryParams(new QueryStringDecoder(httpRequest.uri())
                    .parameters()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0))));
        } else if(msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            reqBuf = Unpooled.copiedBuffer(reqBuf, httpContent.content());
            if(msg instanceof LastHttpContent) {
                handleRequest(httpContent)
                        .doOnNext(httpServerResponse -> {
                            logger.info("{} {} {}", ctx.channel().remoteAddress(), httpServerResponse.getStatus().code(), httpServerResponse.getBody());
                            writeResponse(ctx, httpServerResponse);
                        })
                        .subscribe(new ErrorHandlingSubscriber<>());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception caught on {}", ctx.channel().remoteAddress());
        cause.printStackTrace();
        ctx.close();
    }

    private Observable<HttpServerResponse> handleRequest(HttpContent httpContent) {
        return Observable.defer(() -> {
            if(httpContent.decoderResult().isFailure())
                return Observable.error(new WebException(HttpResponseStatus.BAD_REQUEST));

            if(reqBuf.readableBytes() > 0) {
                try {
                    NodeUtil.isValidJSON(reqBuf.array());
                } catch(RuntimeException e) {
                    return Observable.error(new WebException(HttpResponseStatus.BAD_REQUEST));
                }

                httpServerRequest.setBody(Optional.of(NodeUtil.getJsonNode(reqBuf.array())));
            }

            return apiManager.handleRequest(httpServerRequest);
        })
                .onErrorResumeNext(err -> {
                    WebException e = (err instanceof WebException) ? (WebException) err : new WebException(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    return Observable.just(e.toHttpServerResponse());
                });
    }

    private void writeResponse(ChannelHandlerContext ctx, HttpServerResponse res) {
        ByteBuf content = Unpooled.copiedBuffer(NodeUtil.writeValueAsBytes(res.getBody()));
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, res.getStatus(), content);
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        ctx.write(httpResponse);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
