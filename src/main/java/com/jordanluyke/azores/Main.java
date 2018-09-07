package com.jordanluyke.azores;

import com.jordanluyke.azores.audiocontexts.ToneContext;
import com.jordanluyke.azores.audiocontexts.AmplitudeModulationContext;
import com.jordanluyke.azores.audiocontexts.FrequencyModulationContext;

public class Main {

    public static void main(String[] args) {
        ToneContext.builder()
                .frequency(528)
                .build()
                .configure()
                .start();

//        AmplitudeModulationContext.builder()
//                .carrierFrequency(528)
//                .modulatorFrequency(3)
//                .build()
//                .configure()
//                .start();

//        FrequencyModulationContext.builder()
//                .carrierFrequency(528)
//                .modulatorFrequency(3)
//                .build()
//                .configure()
//                .start();
    }
}
