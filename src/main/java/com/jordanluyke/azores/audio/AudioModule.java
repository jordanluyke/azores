package com.jordanluyke.azores.audio;

import com.google.inject.AbstractModule;

public class AudioModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AudioManager.class).to(AudioManagerImpl.class);
    }
}
