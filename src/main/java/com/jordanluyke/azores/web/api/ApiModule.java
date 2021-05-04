package com.jordanluyke.azores.web.api;

import com.google.inject.AbstractModule;

public class ApiModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ApiManager.class).to(ApiManagerImpl.class);
    }
}
