package com.jordanluyke.azores.audio;

import com.google.inject.Inject;
import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.audio.model.AudioType;
import com.jordanluyke.azores.audio.tone.ToneContext;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@NoArgsConstructor(onConstructor = @__(@Inject))
public class AudioManagerImpl implements AudioManager {
    private static final Logger logger = LogManager.getLogger(AudioManager.class);

    private AudioContext audioContext = new ToneContext();

    @Override
    public Observable<Void> setTone(double frequency) {
        return Observable.defer(() -> {
            if(audioContext.getAudioType() != AudioType.TONE) {
                audioContext.stop();
                audioContext = new ToneContext();
            }
            audioContext.set(frequency);
            return Observable.empty();
        });
    }
}
