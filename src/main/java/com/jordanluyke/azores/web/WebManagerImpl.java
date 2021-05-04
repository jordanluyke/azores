package com.jordanluyke.azores.web;

import com.google.inject.Inject;
import com.jordanluyke.azores.web.netty.NettyServerInitializer;
import io.reactivex.rxjava3.core.Completable;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class WebManagerImpl implements WebManager {
    private static final Logger logger = LogManager.getLogger(WebManager.class);

    private NettyServerInitializer nettyServerInitializer;

    @Override
    public Completable init() {
        return nettyServerInitializer.init();
    }
}
