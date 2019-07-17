package com.jordanluyke.azores.audio.tone;

import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.audio.model.AudioType;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class ToneContext extends AudioContext {
    private static final Logger logger = LogManager.getLogger(ToneContext.class);

    private Synthesizer synth = JSyn.createSynthesizer();
    private SineOscillator osc = new SineOscillator();
    private LineOut lineOut = new LineOut();
    @Getter
    private AudioType audioType = AudioType.TONE;

    public void set(double frequency) {
        if(synth.isRunning())
            osc.frequency.set(frequency);
        else
            start(frequency);
    }

    @Override
    protected void start(double frequency) {
        synth.add(osc);
        synth.add(lineOut);

        osc.output.connect(0, lineOut.input, 0);
        osc.output.connect(0, lineOut.input, 1);
        osc.amplitude.set(1);

        synth.start();
        osc.start();
        lineOut.start();

        osc.frequency.set(frequency);
    }

    @Override
    public void stop() {
        osc.stop();
        lineOut.stop();
        synth.remove(osc);
        synth.remove(lineOut);
        synth.stop();
    }
}
