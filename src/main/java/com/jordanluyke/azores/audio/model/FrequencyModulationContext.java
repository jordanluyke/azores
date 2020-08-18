package com.jordanluyke.azores.audio.model;

import com.jsyn.unitgen.SineOscillatorPhaseModulated;
import lombok.Getter;

import java.time.ZoneId;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
public class FrequencyModulationContext extends ModulationContext {
    private final SineOscillatorPhaseModulated carrierOscillator = new SineOscillatorPhaseModulated();
    private final AudioType audioType = AudioType.FM;

    public FrequencyModulationContext(double carrierFrequency, double modulatorFrequency) {
        super(carrierFrequency, modulatorFrequency);
    }

    public FrequencyModulationContext(double carrierFrequency, double modulatorFrequency, ZoneId zoneId, String from, String to) {
        super(carrierFrequency, modulatorFrequency, zoneId, from, to);
    }
}
