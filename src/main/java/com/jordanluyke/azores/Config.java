package com.jordanluyke.azores;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Getter
@Setter
@Singleton
public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class);

    private SslContext sslCtx;
    private int port = 8443;
    private Injector injector;

    public Config() {
        load();
        logger.info("Config loaded");
    }

    private void load() {
//        var cp = new Properties();
//        try {
//            cp.load(new FileInputStream("config/config.properties"));
//        } catch(IOException e1) {
//            throw new RuntimeException("Unable to load config.properties file");
//        }

        try {
            var cert = Path.of("config/cert.pem");
            var key = Path.of("config/key.pem");
            if(!cert.toFile().exists() || !key.toFile().exists()) {
                Files.createDirectories(Path.of("config"));
                var notBefore = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
                var notAfter = new GregorianCalendar(2030, Calendar.JANUARY, 1).getTime();
                var ssc = new SelfSignedCertificate(notBefore, notAfter);
                Files.copy(ssc.certificate().toPath(), cert);
                Files.copy(ssc.privateKey().toPath(), key);
            }
            var certFile = new FileInputStream(cert.toFile());
            var keyFile = new FileInputStream(key.toFile());
            sslCtx = SslContextBuilder.forServer(certFile, keyFile).build();
        } catch(CertificateException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
