package com.jordanluyke.azores.web.api.model;

import java.util.List;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public interface Api {

    List<HttpRoute> getHttpRoutes();
}
