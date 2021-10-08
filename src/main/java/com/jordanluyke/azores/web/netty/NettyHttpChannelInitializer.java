package com.jordanluyke.azores.web.netty;

import com.google.inject.Inject;
import com.jordanluyke.azores.Config;
import com.jordanluyke.azores.web.api.ApiManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class NettyHttpChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LogManager.getLogger(NettyHttpChannelInitializer.class);

    private Config config;
    private ApiManager apiManager;

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline()
                .addLast(config.getSslCtx().newHandler(channel.alloc()))
//                .addLast(new JdkZlibEncoder());
//                .addLast(new JdkZlibDecoder());
                .addLast(new HttpServerCodec())
                .addLast(new HttpContentCompressor())
                .addLast(new NettyHttpChannelInboundHandler(apiManager));
    }
}
