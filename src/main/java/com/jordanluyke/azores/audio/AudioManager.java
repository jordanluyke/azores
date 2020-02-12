package com.jordanluyke.azores.audio;

import com.jordanluyke.azores.audio.model.AudioContext;
import io.reactivex.rxjava3.core.Single;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface AudioManager {

    AudioContext getAudioContext();

    Single<AudioContext> setTone(double frequency);

    Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency);

    Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency);
}
