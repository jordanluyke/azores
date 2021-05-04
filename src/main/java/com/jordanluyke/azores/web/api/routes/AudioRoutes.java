package com.jordanluyke.azores.web.api.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.jordanluyke.azores.audio.AudioManager;
import com.jordanluyke.azores.audio.dto.FrequenciesRequest;
import com.jordanluyke.azores.audio.dto.FrequenciesResponse;
import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.util.NodeUtil;
import com.jordanluyke.azores.web.api.model.HttpRouteHandler;
import com.jordanluyke.azores.web.model.FieldRequiredException;
import com.jordanluyke.azores.web.model.HttpServerRequest;
import com.jordanluyke.azores.web.model.WebException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.rxjava3.core.Single;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class AudioRoutes {
    private static final Logger logger = LogManager.getLogger(AudioRoutes.class);

    public static class GetFrequencies implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Single<FrequenciesResponse> handle(Single<HttpServerRequest> o) {
            return audioManager.getFrequencies()
                    .toList()
                    .map(audioContexts -> new FrequenciesResponse(audioContexts, audioManager.isActive()));
        }
    }

    public static class SetFrequencies implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Single<FrequenciesResponse> handle(Single<HttpServerRequest> o) {
            return o.flatMap(req -> {
                Optional<JsonNode> body = req.getBody();
                if(body.isEmpty())
                    throw new WebException(HttpResponseStatus.BAD_REQUEST, "Body missing");
                FrequenciesRequest frequenciesRequest = NodeUtil.parseNodeInto(FrequenciesRequest.class, body.get());
                if(frequenciesRequest.getFrequencies().isEmpty())
                    throw new FieldRequiredException("frequencies");
                return audioManager.setFrequencies(frequenciesRequest)
                        .toList()
                        .map(audioContexts -> new FrequenciesResponse(audioContexts, audioManager.isActive()));
            });
        }
    }

//                Optional<String> type = NodeUtil.getString("type", body);
//
//                AudioType audioType = AudioType.TONE;
//                try {
//                    if(type.isPresent())
//                        audioType = AudioType.valueOf(type.get().toUpperCase());
//                } catch(IllegalArgumentException e) {
//                    return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST));
//                }
//
//                Optional<ZoneId> zoneId = Optional.empty();
//                Optional<String> zone = NodeUtil.getString("zone", body);
//                if(zone.isPresent()) {
//                    try {
//                        zoneId = Optional.of(ZoneId.of(zone.get()));
//                    } catch(DateTimeException e) {
//                        return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST, "Invalid zone. Example: America/Denver"));
//                    }
//                }
//                Optional<String> from = NodeUtil.getString("from", body);
//                Optional<String> to = NodeUtil.getString("to", body);
//                if(zoneId.isPresent() && (from.isEmpty() || to.isEmpty()))
//                    return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST, "zone present but from/to empty"));
//                if((from.isPresent() || to.isPresent()) && zoneId.isEmpty())
//                    return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST, "from/to present but zone empty"));
//                if(zoneId.isPresent()) {
//                    try {
//                        audioManager.getTimeFormat().parse(from.get());
//                        audioManager.getTimeFormat().parse(to.get());
//                    } catch(DateTimeParseException e) {
//                        return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST, "Bad time format. Example: 09:00"));
//                    }
//                }
//
//                if(audioType == AudioType.TONE) {
//                    Optional<String> frequency = NodeUtil.getString("frequency", body);
//                    if(frequency.isEmpty())
//                        return Single.error(new FieldRequiredException("frequency"));
//                    try {
//                        if(zoneId.isPresent())
//                            return audioManager.setTone(Double.parseDouble(frequency.get()), from.get(), to.get(), zoneId.get());
//                        return audioManager.setTone(Double.parseDouble(frequency.get()));
//                    } catch(NumberFormatException e) {
//                        return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST));
//                    }
//                } else if(audioType == AudioType.AM || audioType == AudioType.FM) {
//                    Optional<String> carrierFrequency = NodeUtil.getString("carrierFrequency", body);
//                    Optional<String> modulatorFrequency = NodeUtil.getString("modulatorFrequency", body);
//                    if(carrierFrequency.isEmpty())
//                        return Single.error(new FieldRequiredException("carrierFrequency"));
//                    if(modulatorFrequency.isEmpty())
//                        return Single.error(new FieldRequiredException("modulatorFrequency"));
//                    try {
//                        double carrierFrequencyDouble = Double.parseDouble(carrierFrequency.get());
//                        double modulatorFrequencyDouble = Double.parseDouble(modulatorFrequency.get());
//                        if(audioType == AudioType.AM) {
//                            if(zoneId.isPresent())
//                                return audioManager.setAM(carrierFrequencyDouble, modulatorFrequencyDouble, from.get(), to.get(), zoneId.get());
//                            return audioManager.setAM(carrierFrequencyDouble, modulatorFrequencyDouble);
//                        }
//                        if(zoneId.isPresent())
//                            return audioManager.setFM(carrierFrequencyDouble, modulatorFrequencyDouble, from.get(), to.get(), zoneId.get());
//                        return audioManager.setFM(carrierFrequencyDouble, modulatorFrequencyDouble);
//                    } catch(NumberFormatException e) {
//                        return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST));
//                    }
//                }
//
//                return Single.error(new WebException(HttpResponseStatus.BAD_REQUEST));
//            });

    public static class StartFrequencies implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Single<FrequenciesResponse> handle(Single<HttpServerRequest> o) {
            return audioManager.startFrequencies()
                    .map(audioContexts -> new FrequenciesResponse(audioContexts, audioManager.isActive()));
        }
    }

    public static class StopFrequencies implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Single<FrequenciesResponse> handle(Single<HttpServerRequest> o) {
            return audioManager.stopFrequencies()
                    .map(audioContexts -> new FrequenciesResponse(audioContexts, audioManager.isActive()));
        }
    }
}
