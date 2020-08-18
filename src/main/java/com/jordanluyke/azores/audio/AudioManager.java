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
    Single<ToneContext> setTone(double frequency);
    Single<ToneContext> setTone(double frequency, ZoneId zoneId, String from, String to);
    Single<AmplitudeModulationContext> setAM(double carrierFrequency, double modulatorFrequency);
    Single<AmplitudeModulationContext> setAM(double carrierFrequency, double modulatorFrequency, ZoneId zoneId, String from, String to);
    Single<FrequencyModulationContext> setFM(double carrierFrequency, double modulatorFrequency);
    Single<FrequencyModulationContext> setFM(double carrierFrequency, double modulatorFrequency, ZoneId zoneId, String from, String to);
}
