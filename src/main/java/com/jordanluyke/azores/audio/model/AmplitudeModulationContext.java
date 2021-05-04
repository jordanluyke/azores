package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.UnitOscillator;
import lombok.Getter;

import java.time.ZoneId;

@Getter
public class AmplitudeModulationContext extends ModulationContext {
    private final FrequencyType type = FrequencyType.AM;
    @JsonIgnore private UnitOscillator carrierOscillator = new SineOscillator();

    public AmplitudeModulationContext(double carrierFrequency, double modulatorFrequency) {
        super(carrierFrequency, modulatorFrequency);
    }

//    public AmplitudeModulationContext(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId) {
//        super(carrierFrequency, modulatorFrequency, from, to, zoneId);
//    }
}
