package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsyn.unitgen.SineOscillator;
import lombok.Getter;

import java.time.ZoneId;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
public class AmplitudeModulationContext extends ModulationContext {
    @JsonIgnore private final SineOscillator carrierOscillator = new SineOscillator();
    private final AudioType type = AudioType.AM;

    public AmplitudeModulationContext(double carrierFrequency, double modulatorFrequency) {
        super(carrierFrequency, modulatorFrequency);
    }

    public AmplitudeModulationContext(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId) {
        super(carrierFrequency, modulatorFrequency, from, to, zoneId);
    }
}
