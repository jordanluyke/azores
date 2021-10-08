package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.UnitOscillator;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

@Getter
@Setter
@ToString
public class ToneContext extends AudioContext {
    private static final Logger logger = LogManager.getLogger(ToneContext.class);

    private final FrequencyType frequencyType = FrequencyType.TONE;
    @JsonIgnore private UnitOscillator oscillator;
    private double frequency;

    public ToneContext(double frequency, WaveType waveType) {
        this.frequency = frequency;
        this.waveType = waveType;
        this.oscillator = AudioContext.getOscillatorFromWaveType(waveType);
    }

    public ToneContext(double frequency, WaveType waveType, Optional<LocalTime> from, Optional<LocalTime> to, Optional<ZoneId> zoneId) {
        this.frequency = frequency;
        this.waveType = waveType;
        this.oscillator = AudioContext.getOscillatorFromWaveType(waveType);
        this.from = from;
        this.to = to;
        this.zoneId = zoneId;
    }
}
