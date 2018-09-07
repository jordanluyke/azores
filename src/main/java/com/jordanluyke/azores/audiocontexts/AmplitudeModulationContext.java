package com.jordanluyke.azores.audiocontexts;

import com.jordanluyke.azores.audiocontexts.model.BaseContext;
import com.jordanluyke.azores.audiocontexts.model.WaveType;
import com.jordanluyke.azores.util.SynthUtil;
import com.jsyn.unitgen.UnitOscillator;
import lombok.*;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmplitudeModulationContext extends BaseContext {

    private double carrierFrequency;
    private double modulatorFrequency;
    @Builder.Default private WaveType waveType = WaveType.SINE;
    @Builder.Default private double amplitude = 1;

    public AmplitudeModulationContext configure() {
        UnitOscillator carrierOscillator = SynthUtil.createOscillator(waveType);
        UnitOscillator modulatorOscillator = SynthUtil.createOscillator(waveType);

        synthesizer.add(carrierOscillator);
        synthesizer.add(modulatorOscillator);
        synthesizer.add(lineOut);

        carrierOscillator.frequency.set(carrierFrequency);
        carrierOscillator.amplitude.set(amplitude);

        modulatorOscillator.frequency.set(modulatorFrequency / 2);
        modulatorOscillator.amplitude.set(amplitude);

        carrierOscillator.output.connect(modulatorOscillator.amplitude);

        modulatorOscillator.output.connect(0, lineOut.input, 0);
        modulatorOscillator.output.connect(0, lineOut.input, 1);

        return this;
    }
}
