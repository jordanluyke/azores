package com.jordanluyke.azores.web.api.model;

import com.jordanluyke.azores.web.model.HttpServerRequest;
import io.reactivex.rxjava3.core.Single;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface HttpRouteHandler {

    Single<?> handle(Single<HttpServerRequest> o);
}
