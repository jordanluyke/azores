package com.jordanluyke.azores.util;

import com.jordanluyke.azores.audiocontexts.model.WaveType;
import com.jsyn.unitgen.*;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class SynthUtil {

    public static UnitOscillator createOscillator(WaveType waveType) {
        switch (waveType) {
            case SINE:
                return new SineOscillator();
            case SQUARE:
                return new SquareOscillator();
            case SAWTOOTH:
                return new SawtoothOscillator();
            case TRIANGLE:
                return new TriangleOscillator();
            default:
                throw new RuntimeException("Invalid wave type");
        }
    }
}
