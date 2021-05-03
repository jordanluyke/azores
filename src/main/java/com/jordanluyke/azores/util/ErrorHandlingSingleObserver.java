package com.jordanluyke.azores.util;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import org.apache.logging.log4j.LogManager;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class ErrorHandlingSingleObserver<T extends Object> implements SingleObserver<T> {
    private Class<?> loggerClass;

    public ErrorHandlingSingleObserver() {
        loggerClass = getClass();
    }

    @Override
    public void onSuccess(T t) {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        LogManager.getLogger(loggerClass).error("Error: {}", e.getMessage());
    }

    @Override
    public void onSubscribe(Disposable disposable) {
    }
}
