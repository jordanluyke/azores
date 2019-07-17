package com.jordanluyke.azores.web.api;

import com.jordanluyke.azores.web.model.HttpServerRequest;
import com.jordanluyke.azores.web.model.HttpServerResponse;
import rx.Observable;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface ApiManager {

    Observable<HttpServerResponse> handleRequest(HttpServerRequest request);
}
