package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public abstract class AudioContext {

    public abstract AudioType getAudioType();
    public abstract void start();
    public abstract void stop();
    public abstract ObjectNode getJsonInfo();
}
