package com.jordanluyke.azores.web.api.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.jordanluyke.azores.audio.AudioManager;
import com.jordanluyke.azores.audio.dto.FrequenciesRequest;
import com.jordanluyke.azores.audio.dto.FrequenciesResponse;
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
        public Single<FrequenciesResponse> handle(HttpServerRequest o) {
            return audioManager.getFrequencies()
                    .toList()
                    .map(audioContexts -> new FrequenciesResponse(audioContexts, audioManager.isEnabled()));
        }
    }

    public static class SetFrequencies implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Single<FrequenciesResponse> handle(HttpServerRequest req) {
            return Single.defer(() -> {
                Optional<JsonNode> body = req.getBody();
                if(body.isEmpty())
                    throw new WebException(HttpResponseStatus.BAD_REQUEST, "Body missing");
                if(NodeUtil.get("frequencies", body.get()).isEmpty())
                    throw new FieldRequiredException("frequencies");
                FrequenciesRequest frequenciesRequest;
                try {
                    frequenciesRequest = NodeUtil.parseNodeInto(FrequenciesRequest.class, body.get());
                } catch(FieldRequiredException | JsonProcessingException e) {
                    throw new WebException(HttpResponseStatus.BAD_REQUEST, "Error: could not parse request");
                }
                if(frequenciesRequest.getFrequencies().isEmpty())
                    throw new WebException(HttpResponseStatus.BAD_REQUEST, "frequencies list empty");
                return audioManager.setFrequencies(frequenciesRequest)
                        .toList()
                        .map(audioContexts -> new FrequenciesResponse(audioContexts, audioManager.isEnabled()));
            });
        }
    }

    public static class StartFrequencies implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Single<FrequenciesResponse> handle(HttpServerRequest o) {
            return audioManager.startFrequencies()
                    .map(audioContexts -> new FrequenciesResponse(audioContexts, audioManager.isEnabled()));
        }
    }

    public static class StopFrequencies implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Single<FrequenciesResponse> handle(HttpServerRequest o) {
            return audioManager.stopFrequencies()
                    .map(audioContexts -> new FrequenciesResponse(audioContexts, audioManager.isEnabled()));
        }
    }
}
