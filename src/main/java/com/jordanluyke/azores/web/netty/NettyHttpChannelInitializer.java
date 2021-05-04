package com.jordanluyke.azores.web.netty;

import com.google.inject.Inject;
import com.jordanluyke.azores.Config;
import com.jordanluyke.azores.web.api.ApiManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
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
        ChannelPipeline pipeline = channel.pipeline();
//        pipeline.addLast(new JdkZlibEncoder());
//        pipeline.addLast(new JdkZlibDecoder());
        pipeline.addLast(new HttpServerCodec())
                .addLast(new HttpContentCompressor())
                .addLast(new NettyHttpChannelInboundHandler(apiManager));
    }
}
