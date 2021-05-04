package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.SineOscillatorPhaseModulated;
import lombok.Getter;

import java.time.ZoneId;

@Getter
public class FrequencyModulationContext extends ModulationContext {
    private final FrequencyType type = FrequencyType.FM;
    @JsonIgnore private SineOscillatorPhaseModulated carrierOscillator = new SineOscillatorPhaseModulated();

    public FrequencyModulationContext(double carrierFrequency, double modulatorFrequency) {
        super(carrierFrequency, modulatorFrequency);
    }

//    public FrequencyModulationContext(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zone) {
//        super(carrierFrequency, modulatorFrequency, from, to, zone);
//    }
}
