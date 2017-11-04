package com.jordanluyke.azores.audiocontexts.model;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.UnitOscillator;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public abstract class BaseContext {
    protected Synthesizer synthesizer = JSyn.createSynthesizer();
    protected LineOut lineOut = new LineOut();

    public void start() {
        synthesizer.start();
        lineOut.start();
    }

    public void stop() {
        lineOut.stop();
        synthesizer.stop();
    }
}
