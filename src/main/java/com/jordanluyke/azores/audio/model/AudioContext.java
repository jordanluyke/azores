package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.util.Optional;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
@Setter
public abstract class AudioContext {
    protected AudioType audioType;
    protected Optional<ZoneId> zoneId = Optional.empty();
    protected Optional<String> from = Optional.empty();
    protected Optional<String> to = Optional.empty();
    public abstract ObjectNode getInfo();
}
