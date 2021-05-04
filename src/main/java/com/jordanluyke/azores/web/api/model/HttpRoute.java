package com.jordanluyke.azores.web.api.model;

import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpRoute {
    private String path;
    private HttpMethod method;
    private Class<? extends HttpRouteHandler> handler;
}
