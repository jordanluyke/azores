package com.jordanluyke.azores;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;

public class Main {

    public static void main(String[] args) {
        Synthesizer synth = JSyn.createSynthesizer();
        SineOscillator osc = new SineOscillator();
        LineOut lineOut = new LineOut();

        synth.add(osc);
        synth.add(lineOut);

        osc.output.connect(0, lineOut.input, 0);
        osc.output.connect(0, lineOut.input, 1);
        osc.amplitude.set(1);

        synth.start();
        osc.start();
        lineOut.start();

        osc.frequency.set(3);

//        osc.stop();
//        lineOut.stop();
//        synth.remove(osc);
//        synth.remove(lineOut);
//        synth.stop();
    }
}
