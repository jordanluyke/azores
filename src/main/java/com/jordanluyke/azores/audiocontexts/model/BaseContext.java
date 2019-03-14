package com.jordanluyke.azores.audiocontexts.model;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
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
        int inputCannels = 2;
        int outputChannels = 2;
        synthesizer.start(44100, AudioDeviceManager.USE_DEFAULT_DEVICE, inputCannels, AudioDeviceManager.USE_DEFAULT_DEVICE, outputChannels);
        lineOut.start();
    }

    public void stop() {
        lineOut.stop();
        synthesizer.stop();
    }
}
