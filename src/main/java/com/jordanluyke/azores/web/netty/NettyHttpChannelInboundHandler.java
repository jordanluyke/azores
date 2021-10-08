package com.jordanluyke.azores.web.netty;

import com.jordanluyke.azores.util.ErrorHandlingSingleObserver;
import com.jordanluyke.azores.util.NodeUtil;
import com.jordanluyke.azores.web.api.ApiManager;
import com.jordanluyke.azores.web.model.HttpServerRequest;
import com.jordanluyke.azores.web.model.HttpServerResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.net.SocketException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

public class NettyHttpChannelInboundHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger logger = LogManager.getLogger(NettyHttpChannelInboundHandler.class);

    private ApiManager apiManager;

    private ByteBuf content = Unpooled.buffer();
    private HttpServerRequest req = new HttpServerRequest();

    public NettyHttpChannelInboundHandler(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if(msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            URI uri = new URI(httpRequest.uri().replace("\\", ""));

            req.setPath(uri.getRawPath());
            req.setMethod(httpRequest.method());
            req.setHeaders(httpRequest.headers()
                    .entries()
                    .stream()
                    .collect(Collectors.toMap(key -> key.getKey().toLowerCase(), Map.Entry::getValue)));
            req.setQueryParams(new QueryStringDecoder(uri)
                    .parameters()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0))));
        } else if(msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            content = Unpooled.copiedBuffer(content, httpContent.content());
            if(msg instanceof LastHttpContent) {
                req.setContent(content.array());
                content.release();
                logger.debug("HttpRequest: {} {} {}", ctx.channel().remoteAddress(), req.getMethod(), req.getPath());
                apiManager.handleRequest(req)
                        .doOnSuccess(res -> {
                            logger.debug("HttpResponse: {} {} {} {} {}", ctx.channel().remoteAddress(), req.getMethod(), req.getPath(), res.getStatus().code(), res.getBody());
                            writeResponse(ctx, res);
                        })
                        .subscribe(new ErrorHandlingSingleObserver<>());
            }
        }
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        logger.info("channelReadComplete");
////        ctx.close();
//        super.channelReadComplete(ctx);
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(!(cause instanceof SocketException) && !(cause.getCause() instanceof SSLException)) {
            var causeClass = cause.getCause() == null ? cause.getClass() : cause.getCause().getClass();
            logger.error("Exception caught on {} {}: {}", ctx.channel().remoteAddress(), causeClass.getSimpleName(), cause.getMessage());
//            cause.printStackTrace();
        }
        ctx.close();
    }

    private void writeResponse(ChannelHandlerContext ctx, HttpServerResponse res) {
        ByteBuf content = Unpooled.copiedBuffer(NodeUtil.writeValueAsBytes(res.getBody()));
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, res.getStatus(), content);
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        res.getHeaders().forEach((k, v) -> httpResponse.headers().set(k, v));
        ctx.write(httpResponse);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
