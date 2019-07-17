package com.jordanluyke.azores.audio.model;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public abstract class AudioContext {

    public abstract void set(double frequency);
    public abstract AudioType getAudioType();
    protected abstract void start(double frequency);
    public abstract void stop();
}
