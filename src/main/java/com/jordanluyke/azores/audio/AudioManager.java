package com.jordanluyke.azores.audio;

import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.audio.model.AmplitudeModulationContext;
import com.jordanluyke.azores.audio.model.FrequencyModulationContext;
import com.jordanluyke.azores.audio.model.ToneContext;
import io.reactivex.rxjava3.core.Single;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface AudioManager {
    AudioContext getAudioContext();
    DateTimeFormatter getTimeFormat();
    Single<AudioContext> setTone(double frequency);
    Single<AudioContext> setTone(double frequency, String from, String to, ZoneId zoneId);
    Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency);
    Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId);
    Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency);
    Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId);
    Single<AudioContext> enable();
    Single<AudioContext> disable();
}
