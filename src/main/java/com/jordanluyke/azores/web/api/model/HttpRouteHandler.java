package com.jordanluyke.azores.web.api.model;

import com.jordanluyke.azores.web.model.HttpServerRequest;
import io.reactivex.rxjava3.core.Single;

public interface HttpRouteHandler {
    Single<?> handle(HttpServerRequest request);
}
