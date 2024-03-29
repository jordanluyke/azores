package com.jordanluyke.azores.web.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class HttpServerRequest {
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Optional<JsonNode> body = Optional.empty();
    private byte[] content;
    private String path;
    private HttpMethod method;
}
