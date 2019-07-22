package com.jordanluyke.azores.audio;

import com.google.inject.Inject;
import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.audio.model.AudioType;
import com.jordanluyke.azores.audio.tone.AmplitudeModulationContext;
import com.jordanluyke.azores.audio.tone.FrequencyModulationContext;
import com.jordanluyke.azores.audio.tone.ToneContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
@NoArgsConstructor(onConstructor = @__(@Inject))
public class AudioManagerImpl implements AudioManager {
    private static final Logger logger = LogManager.getLogger(AudioManager.class);

    private AudioContext audioContext = new ToneContext();

    @Override
    public Observable<AudioContext> setTone(double frequency) {
        return Observable.defer(() -> {
            if(audioContext.getAudioType() != AudioType.TONE) {
                audioContext.stop();
                audioContext = new ToneContext();
            }
            ToneContext toneContext = (ToneContext) audioContext;
            toneContext.setFrequency(frequency);
            toneContext.start();
            return Observable.just(toneContext);
        });
    }

    @Override
    public Observable<AudioContext> setAM(double carrierFrequency, double modulatorFrequency) {
        return Observable.defer(() -> {
            if(audioContext.getAudioType() != AudioType.AM) {
                audioContext.stop();
                audioContext = new AmplitudeModulationContext();
            }
            AmplitudeModulationContext amContext = (AmplitudeModulationContext) audioContext;
            amContext.setCarrierFrequency(carrierFrequency);
            amContext.setModulatorFrequency(modulatorFrequency);
            amContext.start();
            return Observable.just(amContext);
        });
    }

    @Override
    public Observable<AudioContext> setFM(double carrierFrequency, double modulatorFrequency) {
        return Observable.defer(() -> {
            if(audioContext.getAudioType() != AudioType.FM) {
                audioContext.stop();
                audioContext = new FrequencyModulationContext();
            }
            FrequencyModulationContext fmContext = (FrequencyModulationContext) audioContext;
            fmContext.setCarrierFrequency(carrierFrequency);
            fmContext.setModulatorFrequency(modulatorFrequency);
            fmContext.start();
            return Observable.just(fmContext);
        });
    }
}
