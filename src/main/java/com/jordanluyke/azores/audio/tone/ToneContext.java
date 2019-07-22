package com.jordanluyke.azores.audio.tone;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.audio.model.AudioType;
import com.jordanluyke.azores.audio.model.WaveType;
import com.jordanluyke.azores.util.NodeUtil;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
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
public class ToneContext extends AudioContext {
    private static final Logger logger = LogManager.getLogger(ToneContext.class);

    private final Synthesizer synth = JSyn.createSynthesizer();
    private final SineOscillator osc = new SineOscillator();
    private final LineOut lineOut = new LineOut();
    private final AudioType audioType = AudioType.TONE;

    private double frequency;
    private WaveType waveType = WaveType.SINE;

    @Override
    public void start() {
        osc.frequency.set(frequency);

        if(!synth.isRunning()) {
            synth.add(osc);
            synth.add(lineOut);


            osc.output.connect(0, lineOut.input, 0);
            osc.output.connect(0, lineOut.input, 1);
            osc.amplitude.set(1);

            synth.start();
            osc.start();
            lineOut.start();
        }
    }

    @Override
    public void stop() {
        osc.stop();
        lineOut.stop();
        synth.remove(osc);
        synth.remove(lineOut);
        synth.stop();
    }

    @Override
    public ObjectNode getJsonInfo() {
        ObjectNode body = NodeUtil.mapper.createObjectNode();
        body.put("frequency", frequency);
        body.put("type", audioType.toString());
        return body;
    }
}
