package com.jordanluyke.azores.web.api.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jordanluyke.azores.web.api.model.HttpRouteHandler;
import com.jordanluyke.azores.web.model.HttpServerRequest;
import io.reactivex.rxjava3.core.Single;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SystemRoutes {
    private static final Logger logger = LogManager.getLogger(SystemRoutes.class);

    public static class GetStatus implements HttpRouteHandler {
        @Override
        public Single<ObjectNode> handle(HttpServerRequest req) {
            return Single.defer(() -> {
                ObjectNode body = new ObjectMapper().createObjectNode();
                body.put("time", System.currentTimeMillis());
                body.put("status", "OK");
                return Single.just(body);
            });
        }
    }
}
