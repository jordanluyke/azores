package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY) protected Optional<ZoneId> zone = Optional.empty();
    @JsonInclude(JsonInclude.Include.NON_EMPTY) protected Optional<String> from = Optional.empty();
    @JsonInclude(JsonInclude.Include.NON_EMPTY) protected Optional<String> to = Optional.empty();
    protected boolean on = false;
}
