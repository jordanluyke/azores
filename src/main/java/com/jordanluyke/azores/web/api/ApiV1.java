package com.jordanluyke.azores.web.api;

import com.jordanluyke.azores.web.api.model.Api;
import com.jordanluyke.azores.web.api.model.HttpRoute;
import com.jordanluyke.azores.web.api.routes.AudioRoutes;
import com.jordanluyke.azores.web.api.routes.SystemRoutes;
import io.netty.handler.codec.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
public class ApiV1 implements Api {

    @Override
    public List<HttpRoute> getHttpRoutes() {
        return Arrays.asList(
                new HttpRoute("/status", HttpMethod.GET, SystemRoutes.GetStatus.class),
                new HttpRoute("/frequency", HttpMethod.GET, AudioRoutes.GetFrequency.class),
                new HttpRoute("/frequency", HttpMethod.POST, AudioRoutes.SetFrequency.class)
        );
    }
}
