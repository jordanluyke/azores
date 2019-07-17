package com.jordanluyke.azores;

import rx.Observable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface MainManager {

    Observable<Void> start();
}
