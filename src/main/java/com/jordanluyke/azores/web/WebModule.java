package com.jordanluyke.azores.web;

import com.google.inject.AbstractModule;
import com.jordanluyke.azores.web.api.ApiModule;

public class WebModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WebManager.class).to(WebManagerImpl.class);
        install(new ApiModule());
    }
}
