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
public class ToneContext extends BaseContext {

    private double frequency;
    @Builder.Default private WaveType waveType = WaveType.SINE;
    @Builder.Default private double amplitude = 1;

    public BaseContext configure() {
        UnitOscillator oscillator = SynthUtil.createOscillator(waveType);

        synthesizer.add(oscillator);
        synthesizer.add(lineOut);

        oscillator.frequency.set(frequency);
        oscillator.amplitude.set(amplitude);

        oscillator.output.connect(0, lineOut.input, 0);
        oscillator.output.connect(0, lineOut.input, 1);

        return this;
    }
}
