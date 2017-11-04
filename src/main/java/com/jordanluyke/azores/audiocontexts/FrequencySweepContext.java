package com.jordanluyke.azores.audiocontexts;

import com.jordanluyke.azores.audiocontexts.model.AudioContext;
import com.jordanluyke.azores.audiocontexts.model.BaseContext;
import com.jordanluyke.azores.audiocontexts.model.WaveType;
import com.jordanluyke.azores.util.SynthUtil;
import com.jsyn.unitgen.SineOscillatorPhaseModulated;
import com.jsyn.unitgen.UnitOscillator;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class FrequencySweepContext extends BaseContext implements AudioContext {

    private double carrierFrequency;
    private double amplitude;
    private UnitOscillator modulator;
    private double modulatorFrequency;
    private SineOscillatorPhaseModulated carrier = new SineOscillatorPhaseModulated();

    public FrequencySweepContext(double carrierFrequency, double amplitude, WaveType waveType, double modulatorFrequency) {
        this.carrierFrequency = carrierFrequency;
        this.amplitude = amplitude;
        this.modulator = SynthUtil.createOscillator(waveType);
        this.modulatorFrequency = modulatorFrequency;
    }

    @Override
    public void configure() {
        synthesizer.add(carrier);
        synthesizer.add(modulator);
        synthesizer.add(lineOut);

        carrier.frequency.set(carrierFrequency);
        carrier.amplitude.set(1);

        modulator.frequency.set(modulatorFrequency);
        modulator.amplitude.set(amplitude);

        modulator.output.connect(carrier.modulation);

        carrier.output.connect(0, lineOut.input, 0);
        carrier.output.connect(0, lineOut.input, 1);
    }
}
