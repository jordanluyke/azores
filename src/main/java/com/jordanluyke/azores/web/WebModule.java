package com.jordanluyke.azores.web;

import com.google.inject.AbstractModule;
import com.jordanluyke.azores.web.api.ApiModule;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class WebModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WebManager.class).to(WebManagerImpl.class);
        install(new ApiModule());
    }
}
