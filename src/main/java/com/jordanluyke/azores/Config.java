package com.jordanluyke.azores;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
@Setter
@Singleton
public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class);

    private int port = 8080;
    private Injector injector;
}
