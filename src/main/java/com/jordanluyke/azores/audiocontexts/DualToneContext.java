package com.jordanluyke.azores.audiocontexts;

import com.jordanluyke.azores.audiocontexts.model.AudioContext;
import com.jordanluyke.azores.audiocontexts.model.BaseContext;
import com.jordanluyke.azores.audiocontexts.model.WaveType;
import com.jordanluyke.azores.util.SynthUtil;
import com.jsyn.unitgen.UnitOscillator;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class DualToneContext extends BaseContext implements AudioContext {

    private double frequency1;
    private double frequency2;
    private UnitOscillator oscillator1;
    private UnitOscillator oscillator2;

    public DualToneContext(double frequency1, double frequency2, WaveType waveType) {
        this.frequency1 = frequency1;
        this.frequency2 = frequency2;
        this.oscillator1 = SynthUtil.createOscillator(waveType);
        this.oscillator2 = SynthUtil.createOscillator(waveType);
    }

    @Override
    public void configure() {
        synthesizer.add(oscillator1);
        synthesizer.add(oscillator2);
        synthesizer.add(lineOut);

        oscillator1.frequency.set(frequency1);
        oscillator2.frequency.set(frequency2);
        oscillator1.amplitude.set(1);
        oscillator2.amplitude.set(1);

        oscillator1.output.connect(0, lineOut.input, 0);
        oscillator1.output.connect(0, lineOut.input, 1);
        oscillator2.output.connect(0, lineOut.input, 0);
        oscillator2.output.connect(0, lineOut.input, 1);
    }

    @Override
    public void start() {
        synthesizer.start();
        lineOut.start();
    }

    @Override
    public void stop() {
        lineOut.stop();
        synthesizer.stop();
    }
}
