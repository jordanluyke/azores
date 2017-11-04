package com.jordanluyke.azores.audiocontexts;

import com.jordanluyke.azores.audiocontexts.model.AudioContext;
import com.jordanluyke.azores.audiocontexts.model.BaseContext;
import com.jordanluyke.azores.audiocontexts.model.WaveType;
import com.jordanluyke.azores.util.SynthUtil;
import com.jsyn.unitgen.UnitOscillator;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class VolumeOscillatorContext extends BaseContext implements AudioContext {

    private double toneFrequency;
    private double volumeFrequency;
    private UnitOscillator toneOscillator;
    private UnitOscillator volumeOscillator;

    public VolumeOscillatorContext(double toneFrequency, WaveType waveType, double volumeFrequency) {
        this.toneFrequency = toneFrequency;
        this.volumeFrequency = volumeFrequency;
        this.toneOscillator = SynthUtil.createOscillator(waveType);
        this.volumeOscillator = SynthUtil.createOscillator(waveType);
    }

    @Override
    public void configure() {
        synthesizer.add(toneOscillator);
        synthesizer.add(volumeOscillator);
        synthesizer.add(lineOut);

        toneOscillator.frequency.set(toneFrequency);
        toneOscillator.amplitude.set(1);

        volumeOscillator.frequency.set(volumeFrequency / 2);
        volumeOscillator.amplitude.set(1);

        volumeOscillator.output.connect(toneOscillator.amplitude);

        toneOscillator.output.connect(0, lineOut.input, 0);
        toneOscillator.output.connect(0, lineOut.input, 1);
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
