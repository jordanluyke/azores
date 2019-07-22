package com.jordanluyke.azores.audio;

import com.jordanluyke.azores.audio.model.AudioContext;
import rx.Observable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface AudioManager {

    AudioContext getAudioContext();

    Observable<AudioContext> setTone(double frequency);

    Observable<AudioContext> setAM(double carrierFrequency, double modulatorFrequency);

    Observable<AudioContext> setFM(double carrierFrequency, double modulatorFrequency);
}
