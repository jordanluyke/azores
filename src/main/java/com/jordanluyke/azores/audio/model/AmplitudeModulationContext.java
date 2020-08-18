package com.jordanluyke.azores.audio.model;

import com.jsyn.unitgen.SineOscillator;
import lombok.Getter;

import java.time.ZoneId;
import java.util.Optional;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
public class AmplitudeModulationContext extends ModulationContext {
    private final SineOscillator carrierOscillator = new SineOscillator();
    private final AudioType audioType = AudioType.AM;

    public AmplitudeModulationContext(double carrierFrequency, double modulatorFrequency) {
        super(carrierFrequency, modulatorFrequency);
    }

    public AmplitudeModulationContext(double carrierFrequency, double modulatorFrequency, ZoneId zoneId, String from, String to) {
        super(carrierFrequency, modulatorFrequency, zoneId, from, to);
    }
}
