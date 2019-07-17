package com.jordanluyke.azores.audio;

import rx.Observable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface AudioManager {

    Observable<Void> setTone(double frequency);
}
