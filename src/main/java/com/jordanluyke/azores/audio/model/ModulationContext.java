package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.SineOscillator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZoneId;
import java.util.Optional;

@Getter
@Setter
@ToString
public abstract class ModulationContext extends AudioContext {
    @JsonIgnore private final SineOscillator modulatorOscillator = new SineOscillator();
    private double carrierFrequency;
    private double modulatorFrequency;

    public ModulationContext(double carrierFrequency, double modulatorFrequency) {
        this.carrierFrequency = carrierFrequency;
        this.modulatorFrequency = modulatorFrequency;
    }

    public ModulationContext(double carrierFrequency, double modulatorFrequency, ZoneId zone, String from, String to) {
        this.carrierFrequency = carrierFrequency;
        this.modulatorFrequency = modulatorFrequency;
        this.zone = Optional.of(zone);
        this.from = Optional.of(from);
        this.to = Optional.of(to);
    }

    public abstract SineOscillator getCarrierOscillator();
    public abstract AudioType getType();
}
