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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;

import java.util.Optional;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class AudioRoutes {
    private static final Logger logger = LogManager.getLogger(AudioRoutes.class);

    public static class GetFrequency implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Observable<ObjectNode> handle(Observable<HttpServerRequest> o) {
            return Observable.just(audioManager.getAudioContext().getJsonInfo());
        }
    }

    public static class SetFrequency implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Observable<ObjectNode> handle(Observable<HttpServerRequest> o) {
            return o.flatMap(req -> {
                if(!req.getBody().isPresent())
                    return Observable.error(new WebException(HttpResponseStatus.BAD_REQUEST));
                JsonNode body = req.getBody().get();
                Optional<String> type = NodeUtil.get(body, "type");
                if(!type.isPresent())
                    return Observable.error(new FieldRequiredException("type"));

                if(type.get().equalsIgnoreCase(AudioType.TONE.toString())) {
                    Optional<String> frequency = NodeUtil.get(body, "frequency");
                    if(!frequency.isPresent())
                        return Observable.error(new FieldRequiredException("frequency"));
                    try {
                        return audioManager.setTone(Double.parseDouble(frequency.get()));
                    } catch(NumberFormatException e) {
                        return Observable.error(new WebException(HttpResponseStatus.BAD_REQUEST));
                    }
                }

                if(type.get().equalsIgnoreCase(AudioType.AM.toString())) {
                    Optional<String> carrierFrequency = NodeUtil.get(body, "carrierFrequency");
                    Optional<String> modulatorFrequency = NodeUtil.get(body, "modulatorFrequency");
                    if(!carrierFrequency.isPresent())
                        return Observable.error(new FieldRequiredException("carrierFrequency"));
                    if(!modulatorFrequency.isPresent())
                        return Observable.error(new FieldRequiredException("modulatorFrequency"));
                    try {
                        return audioManager.setAM(Double.parseDouble(carrierFrequency.get()), Double.parseDouble(modulatorFrequency.get()));
                    } catch(NumberFormatException e) {
                        return Observable.error(new WebException(HttpResponseStatus.BAD_REQUEST));
                    }
                }

                if(type.get().equalsIgnoreCase(AudioType.FM.toString())) {
                    Optional<String> carrierFrequency = NodeUtil.get(body, "carrierFrequency");
                    Optional<String> modulatorFrequency = NodeUtil.get(body, "modulatorFrequency");
                    if(!carrierFrequency.isPresent())
                        return Observable.error(new FieldRequiredException("carrierFrequency"));
                    if(!modulatorFrequency.isPresent())
                        return Observable.error(new FieldRequiredException("modulatorFrequency"));
                    try {
                        return audioManager.setFM(Double.parseDouble(carrierFrequency.get()), Double.parseDouble(modulatorFrequency.get()));
                    } catch(NumberFormatException e) {
                        return Observable.error(new WebException(HttpResponseStatus.BAD_REQUEST));
                    }
                }

                return Observable.error(new WebException(HttpResponseStatus.BAD_REQUEST));
            })
                    .defaultIfEmpty(null)
                    .map(AudioContext::getJsonInfo);
        }
    }
}
