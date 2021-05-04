package com.jordanluyke.azores;

import com.google.inject.AbstractModule;
import com.jordanluyke.azores.audio.AudioModule;
import com.jordanluyke.azores.web.WebModule;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MainManager.class).to(MainManagerImpl.class);
        install(new WebModule());
        install(new AudioModule());
    }
}
