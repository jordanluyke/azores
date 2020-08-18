package com.jordanluyke.azores.web.api.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.jordanluyke.azores.audio.AudioManager;
import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.audio.model.AudioType;
import com.jordanluyke.azores.util.NodeUtil;
import com.jordanluyke.azores.web.api.model.HttpRouteHandler;
import com.jordanluyke.azores.web.model.FieldRequiredException;
import com.jordanluyke.azores.web.model.HttpServerRequest;
import com.jordanluyke.azores.web.model.WebException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.rxjava3.core.Single;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class AudioRoutes {
    private static final Logger logger = LogManager.getLogger(AudioRoutes.class);

    public static class GetFrequency implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Single<AudioContext> handle(Single<HttpServerRequest> o) {
            return Single.just(audioManager.getAudioContext());
        }
    }

    public static class SetFrequency implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Single<AudioContext> handle(Single<HttpServerRequest> o) {
            return o.flatMap(req -> {
                if(req.getBody().isEmpty())
                    return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST, "Body missing"));

                JsonNode body = req.getBody().get();
                Optional<String> type = NodeUtil.getString("type", body);
                if(type.isEmpty())
                    return Single.error(new FieldRequiredException("type"));

                AudioType audioType;
                try {
                    audioType = AudioType.valueOf(type.get().toUpperCase());
                } catch(IllegalArgumentException e) {
                    return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST));
                }

                Optional<ZoneId> zoneId = Optional.empty();
                Optional<String> zone = NodeUtil.getString("zone", body);
                if(zone.isPresent()) {
                    try {
                        zoneId = Optional.of(ZoneId.of(zone.get()));
                    } catch(DateTimeException e) {
                        return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST, "Invalid zone. Example: America/Denver"));
                    }
                }
                Optional<String> from = NodeUtil.getString("from", body);
                Optional<String> to = NodeUtil.getString("to", body);
                if(zoneId.isPresent() && (from.isEmpty() || to.isEmpty()))
                    return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST, "zone present but from/to empty"));
                if((from.isPresent() || to.isPresent()) && zoneId.isEmpty())
                    return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST, "from/to present but zone empty"));
                if(zoneId.isPresent()) {
                    try {
                        audioManager.getTimeFormat().parse(from.get());
                        audioManager.getTimeFormat().parse(to.get());
                    } catch(DateTimeParseException e) {
                        return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST, "Bad time format. Example: 09:00"));
                    }
                }

                if(audioType == AudioType.TONE) {
                    Optional<String> frequency = NodeUtil.getString("frequency", body);
                    if(frequency.isEmpty())
                        return Single.error(new FieldRequiredException("frequency"));
                    try {
                        if(zoneId.isPresent())
                            return audioManager.setTone(Double.parseDouble(frequency.get()), zoneId.get(), from.get(), to.get());
                        return audioManager.setTone(Double.parseDouble(frequency.get()));
                    } catch(NumberFormatException e) {
                        return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST));
                    }
                } else if(audioType == AudioType.AM || audioType == AudioType.FM) {
                    Optional<String> carrierFrequency = NodeUtil.getString("carrierFrequency", body);
                    Optional<String> modulatorFrequency = NodeUtil.getString("modulatorFrequency", body);
                    if(carrierFrequency.isEmpty())
                        return Single.error(new FieldRequiredException("carrierFrequency"));
                    if(modulatorFrequency.isEmpty())
                        return Single.error(new FieldRequiredException("modulatorFrequency"));
                    try {
                        double carrierFrequencyDouble = Double.parseDouble(carrierFrequency.get());
                        double modulatorFrequencyDouble = Double.parseDouble(modulatorFrequency.get());
                        if(audioType == AudioType.AM) {
                            if(zoneId.isPresent())
                                return audioManager.setAM(carrierFrequencyDouble, modulatorFrequencyDouble, zoneId.get(), from.get(), to.get());
                            return audioManager.setAM(carrierFrequencyDouble, modulatorFrequencyDouble);
                        }
                        if(zoneId.isPresent())
                            return audioManager.setFM(carrierFrequencyDouble, modulatorFrequencyDouble, zoneId.get(), from.get(), to.get());
                        return audioManager.setFM(carrierFrequencyDouble, modulatorFrequencyDouble);
                    } catch(NumberFormatException e) {
                        return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST));
                    }
                }

                return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST));
            });
        }
    }
}
