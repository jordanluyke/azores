package com.jordanluyke.azores.web.api.routes;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.jordanluyke.azores.audio.AudioManager;
import com.jordanluyke.azores.util.NodeUtil;
import com.jordanluyke.azores.web.api.model.HttpRouteHandler;
import com.jordanluyke.azores.web.model.HttpServerRequest;
import com.jordanluyke.azores.web.model.WebException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class AudioRoutes {
    private static final Logger logger = LogManager.getLogger(AudioRoutes.class);

    public static class SetTone implements HttpRouteHandler {
        @Inject protected AudioManager audioManager;
        @Override
        public Observable<ObjectNode> handle(Observable<HttpServerRequest> o) {
            return o.flatMap(req -> {
                String frequency = req.getQueryParams().get("frequency");
                try {
                    return audioManager.setTone(Double.parseDouble(frequency));
                } catch(NumberFormatException e) {
                    return Observable.error(new WebException(HttpResponseStatus.BAD_REQUEST));
                }
            })
                    .defaultIfEmpty(null)
                    .map(Void -> NodeUtil.mapper.createObjectNode());
        }
    }
}
