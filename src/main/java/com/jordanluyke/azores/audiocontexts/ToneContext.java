package com.jordanluyke.azores.audiocontexts;

import com.jordanluyke.azores.audiocontexts.model.AudioContext;
import com.jordanluyke.azores.audiocontexts.model.BaseContext;
import com.jordanluyke.azores.audiocontexts.model.WaveType;
import com.jordanluyke.azores.util.SynthUtil;
import com.jsyn.unitgen.UnitOscillator;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class ToneContext extends BaseContext implements AudioContext {

    private double frequency;
    private UnitOscillator oscillator;
    private double amplitude;

    public ToneContext(double frequency, WaveType waveType) {
        this(frequency, waveType, 1);
    }

    public ToneContext(double frequency, WaveType waveType, double amplitude) {
        this.frequency = frequency;
        this.oscillator = SynthUtil.createOscillator(waveType);
        this.amplitude = amplitude;
    }

    @Override
    public void configure() {
        synthesizer.add(oscillator);
        synthesizer.add(lineOut);

        oscillator.frequency.set(frequency);
        oscillator.amplitude.set(amplitude);

        oscillator.output.connect(0, lineOut.input, 0);
        oscillator.output.connect(0, lineOut.input, 1);
    }
}
