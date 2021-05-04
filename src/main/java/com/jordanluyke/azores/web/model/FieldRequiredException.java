package com.jordanluyke.azores.web.model;

import io.netty.handler.codec.http.HttpResponseStatus;

public class FieldRequiredException extends WebException {
    public static final long serialVersionUID = 103L;

    public FieldRequiredException(String fieldName) {
        this(fieldName, HttpResponseStatus.BAD_REQUEST);
    }

    public FieldRequiredException(String fieldName, HttpResponseStatus status) {
        super(status, "Field required: " + fieldName, FieldRequiredException.class.getSimpleName());
    }
}
