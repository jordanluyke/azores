package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.UnitOscillator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

@Getter
@Setter
@ToString
public abstract class ModulationContext extends AudioContext {
    @JsonIgnore private final UnitOscillator modulatorOscillator = new SineOscillator();
    private double carrierFrequency;
    private double modulatorFrequency;
    private Optional<LocalTime> from = Optional.empty();
    private Optional<LocalTime> to = Optional.empty();
    private Optional<ZoneId> zoneId = Optional.empty();

    public ModulationContext(double carrierFrequency, double modulatorFrequency) {
        this.carrierFrequency = carrierFrequency;
        this.modulatorFrequency = modulatorFrequency;
    }

    public ModulationContext(double carrierFrequency, double modulatorFrequency, Optional<LocalTime> from, Optional<LocalTime> to, Optional<ZoneId> zoneId) {
        this.carrierFrequency = carrierFrequency;
        this.modulatorFrequency = modulatorFrequency;
        this.from = from;
        this.to = to;
        this.zoneId = zoneId;
    }

    public abstract UnitOscillator getCarrierOscillator();
}
