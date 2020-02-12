package com.jordanluyke.azores.web;


import io.reactivex.rxjava3.core.Completable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface WebManager {

    Completable start();
}
