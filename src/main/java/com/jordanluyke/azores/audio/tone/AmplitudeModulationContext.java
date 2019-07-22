package com.jordanluyke.azores.audio.tone;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.audio.model.AudioType;
import com.jordanluyke.azores.audio.model.WaveType;
import com.jordanluyke.azores.util.NodeUtil;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
@Setter
public class AmplitudeModulationContext extends AudioContext {
    private static final Logger logger = LogManager.getLogger(AmplitudeModulationContext.class);

    private final Synthesizer synth = JSyn.createSynthesizer();
    private final SineOscillator carrierOscillator = new SineOscillator();
    private final SineOscillator modulatorOscillator = new SineOscillator();
    private final LineOut lineOut = new LineOut();
    private final AudioType audioType = AudioType.AM;

    private double carrierFrequency;
    private double modulatorFrequency;
    private WaveType waveType = WaveType.SINE;

    @Override
    public void start() {
        carrierOscillator.frequency.set(carrierFrequency);
        modulatorOscillator.frequency.set(modulatorFrequency / 2);

        if(!synth.isRunning()) {
            synth.add(carrierOscillator);
            synth.add(modulatorOscillator);
            synth.add(lineOut);

            carrierOscillator.amplitude.set(1);
            modulatorOscillator.amplitude.set(1);

            carrierOscillator.output.connect(modulatorOscillator.amplitude);
            modulatorOscillator.output.connect(0, lineOut.input, 0);
            modulatorOscillator.output.connect(0, lineOut.input, 1);

            synth.start(44100, AudioDeviceManager.USE_DEFAULT_DEVICE, 2, AudioDeviceManager.USE_DEFAULT_DEVICE, 2);
            carrierOscillator.start();
            modulatorOscillator.start();
            lineOut.start();
        }
    }

    @Override
    public void stop() {
        carrierOscillator.stop();
        modulatorOscillator.stop();
        lineOut.stop();
        synth.remove(carrierOscillator);
        synth.remove(modulatorOscillator);
        synth.remove(lineOut);
        synth.stop();
    }

    @Override
    public ObjectNode getJsonInfo() {
        ObjectNode body = NodeUtil.mapper.createObjectNode();
        body.put("carrierFrequency", carrierFrequency);
        body.put("modulatorFrequency", modulatorFrequency);
        body.put("type", audioType.toString());
        return body;
    }
}
