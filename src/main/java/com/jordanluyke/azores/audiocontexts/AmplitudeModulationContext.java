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

    private double toneFrequency;
    private double amplitudeFrequency;
    @Builder.Default private WaveType waveType = WaveType.SINE;
    @Builder.Default private double amplitude = 1;

    public AmplitudeModulationContext configure() {
        UnitOscillator toneOscillator = SynthUtil.createOscillator(waveType);
        UnitOscillator amplitudeOscillator = SynthUtil.createOscillator(waveType);

        synthesizer.add(toneOscillator);
        synthesizer.add(amplitudeOscillator);
        synthesizer.add(lineOut);

        toneOscillator.frequency.set(toneFrequency);
        toneOscillator.amplitude.set(amplitude);

        amplitudeOscillator.frequency.set(amplitudeFrequency / 2);
        amplitudeOscillator.amplitude.set(amplitude);

        toneOscillator.output.connect(amplitudeOscillator.amplitude);

        amplitudeOscillator.output.connect(0, lineOut.input, 0);
        amplitudeOscillator.output.connect(0, lineOut.input, 1);

        return this;
    }
}
