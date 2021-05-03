package com.jordanluyke.azores.audio;

import com.jordanluyke.azores.audio.dto.FrequenciesRequest;
import com.jordanluyke.azores.audio.model.AudioContext;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface AudioManager {
    Observable<? extends AudioContext> getFrequencies();
    Observable<? extends AudioContext> setFrequencies(FrequenciesRequest request);
//    DateTimeFormatter getTimeFormat();
//    Single<AudioContext> setTone(double frequency);
//    Single<AudioContext> setTone(double frequency, String from, String to, ZoneId zoneId);
//    Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency);
//    Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId);
//    Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency);
//    Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId);
//    Single<AudioContext> enable();
//    Single<AudioContext> disable();
    boolean isEnabled();
}
