package com.jordanluyke.azores.web.api;

import com.google.inject.Inject;
import com.jordanluyke.azores.web.model.HttpServerRequest;
import com.jordanluyke.azores.web.model.HttpServerResponse;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
public class ApiManagerImpl implements ApiManager {
    private static final Logger logger = LogManager.getLogger(ApiManager.class);

    private RouteMatcher routeMatcher;

    @Override
    public Observable<HttpServerResponse> handleRequest(HttpServerRequest request) {
        return routeMatcher.handle(request);
    }
}
