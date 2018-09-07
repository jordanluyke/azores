package com.jordanluyke.azores.audiocontexts;

import com.jordanluyke.azores.audiocontexts.model.BaseContext;
import com.jordanluyke.azores.audiocontexts.model.WaveType;
import com.jordanluyke.azores.util.SynthUtil;
import com.jsyn.unitgen.SineOscillatorPhaseModulated;
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
public class FrequencyModulationContext extends BaseContext {

    private double carrierFrequency;
    private double modulatorFrequency;
    @Builder.Default private WaveType waveType = WaveType.SINE;
    @Builder.Default private double amplitude = 1;

    public FrequencyModulationContext configure() {
        UnitOscillator modulator = SynthUtil.createOscillator(waveType);
        SineOscillatorPhaseModulated carrier = new SineOscillatorPhaseModulated();

        synthesizer.add(carrier);
        synthesizer.add(modulator);
        synthesizer.add(lineOut);

        carrier.frequency.set(carrierFrequency);
        carrier.amplitude.set(amplitude);

        modulator.frequency.set(modulatorFrequency);
        modulator.amplitude.set(amplitude);

        modulator.output.connect(carrier.modulation);

        carrier.output.connect(0, lineOut.input, 0);
        carrier.output.connect(0, lineOut.input, 1);

        return this;
    }
}
