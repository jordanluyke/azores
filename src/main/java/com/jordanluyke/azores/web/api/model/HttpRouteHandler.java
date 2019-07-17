package com.jordanluyke.azores.web.api.model;

import com.jordanluyke.azores.web.model.HttpServerRequest;
import rx.Observable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface HttpRouteHandler {

    Observable<?> handle(Observable<HttpServerRequest> o);
}
