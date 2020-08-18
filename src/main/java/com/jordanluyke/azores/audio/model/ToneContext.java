package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.SineOscillator;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;
import java.util.Optional;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
@Setter
public class ToneContext extends AudioContext {
    private static final Logger logger = LogManager.getLogger(ToneContext.class);

    private final AudioType type = AudioType.TONE;
    @JsonIgnore private final SineOscillator oscillator = new SineOscillator();
    private double frequency;

    public ToneContext(double frequency) {
        this.frequency = frequency;
    }

    public ToneContext(double frequency, ZoneId zone, String from, String to) {
        this.frequency = frequency;
        this.zone = Optional.of(zone);
        this.from = Optional.of(from);
        this.to = Optional.of(to);
    }
}
