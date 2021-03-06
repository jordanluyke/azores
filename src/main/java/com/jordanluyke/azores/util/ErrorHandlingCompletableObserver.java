package com.jordanluyke.azores.util;

import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import org.apache.logging.log4j.LogManager;

public class ErrorHandlingCompletableObserver implements CompletableObserver {
    private Class<?> loggerClass;

    public ErrorHandlingCompletableObserver() {
        loggerClass = getClass();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        LogManager.getLogger(loggerClass).error("Error {}", e.getMessage());
    }

    @Override
    public void onSubscribe(Disposable disposable) {
    }
}