package com.jordanluyke.azores;

import com.jordanluyke.azores.audiocontexts.ToneContext;
import com.jordanluyke.azores.audiocontexts.model.WaveType;

public class Main {

    public static void main(String[] args) {
        ToneContext tone = new ToneContext(3, WaveType.SINE);
        tone.configure();
        tone.start();
    }
}
