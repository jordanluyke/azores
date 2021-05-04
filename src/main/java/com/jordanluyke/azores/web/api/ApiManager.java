package com.jordanluyke.azores.web.api;

import com.jordanluyke.azores.web.model.HttpServerRequest;
import com.jordanluyke.azores.web.model.HttpServerResponse;
import io.reactivex.rxjava3.core.Single;

public interface ApiManager {
    Single<HttpServerResponse> handleRequest(HttpServerRequest request);
}
