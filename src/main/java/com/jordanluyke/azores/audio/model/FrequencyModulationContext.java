package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.SineOscillatorPhaseModulated;
import lombok.Getter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

@Getter
public class FrequencyModulationContext extends ModulationContext {
    private final FrequencyType frequencyType = FrequencyType.FM;
    @JsonIgnore private SineOscillatorPhaseModulated carrierOscillator = new SineOscillatorPhaseModulated();

    public FrequencyModulationContext(double carrierFrequency, double modulatorFrequency) {
        super(carrierFrequency, modulatorFrequency);
    }

    public FrequencyModulationContext(double carrierFrequency, double modulatorFrequency, Optional<LocalTime> from, Optional<LocalTime> to, Optional<ZoneId> zoneId) {
        super(carrierFrequency, modulatorFrequency, from, to, zoneId);
    }
}
