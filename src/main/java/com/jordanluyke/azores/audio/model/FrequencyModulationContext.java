package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.SineOscillatorPhaseModulated;
import lombok.Getter;

import java.time.ZoneId;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
public class FrequencyModulationContext extends ModulationContext {
    @JsonIgnore private final SineOscillatorPhaseModulated carrierOscillator = new SineOscillatorPhaseModulated();
    private final AudioType type = AudioType.FM;

    public FrequencyModulationContext(double carrierFrequency, double modulatorFrequency) {
        super(carrierFrequency, modulatorFrequency);
    }

    public FrequencyModulationContext(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zone) {
        super(carrierFrequency, modulatorFrequency, from, to, zone);
    }
}
