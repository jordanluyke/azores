package com.jordanluyke.azores.audio;

import com.google.inject.AbstractModule;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class AudioModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AudioManager.class).to(AudioManagerImpl.class);
    }
}
