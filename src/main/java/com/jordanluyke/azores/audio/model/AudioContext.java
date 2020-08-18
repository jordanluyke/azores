package com.jordanluyke.azores.audio.model;

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
    protected AudioType type;
    protected Optional<ZoneId> zone = Optional.empty();
    protected Optional<String> from = Optional.empty();
    protected Optional<String> to = Optional.empty();
}
