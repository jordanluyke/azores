package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.UnitOscillator;
import lombok.Getter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

@Getter
public class AmplitudeModulationContext extends ModulationContext {
    private final FrequencyType frequencyType = FrequencyType.AM;
    @JsonIgnore private UnitOscillator carrierOscillator = new SineOscillator();

    public AmplitudeModulationContext(double carrierFrequency, double modulatorFrequency) {
        super(carrierFrequency, modulatorFrequency);
    }

    public AmplitudeModulationContext(double carrierFrequency, double modulatorFrequency, Optional<LocalTime> from, Optional<LocalTime> to, Optional<ZoneId> zoneId) {
        super(carrierFrequency, modulatorFrequency, from, to, zoneId);
    }
}
