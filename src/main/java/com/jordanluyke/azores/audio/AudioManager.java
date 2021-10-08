package com.jordanluyke.azores.audio;

import com.jordanluyke.azores.audio.dto.FrequenciesRequest;
import com.jordanluyke.azores.audio.model.AudioContext;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface AudioManager {
    Completable init();
    Observable<? extends AudioContext> getFrequencies();
    Observable<? extends AudioContext> setFrequencies(FrequenciesRequest request);
    Single<List<AudioContext>> startFrequencies();
    Single<List<AudioContext>> stopFrequencies();
    boolean isEnabled();
}
