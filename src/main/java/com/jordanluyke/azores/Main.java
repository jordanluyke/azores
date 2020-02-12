package com.jordanluyke.azores;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jordanluyke.azores.util.ErrorHandlingCompletableObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Initializing");
        Injector injector = Guice.createInjector(new MainModule());
        injector.getInstance(Config.class)
                .setInjector(injector);
        injector.getInstance(MainManager.class)
                .start()
                .subscribe(new ErrorHandlingCompletableObserver());
    }
}
