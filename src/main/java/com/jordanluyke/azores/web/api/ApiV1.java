package com.jordanluyke.azores.web.api;

import com.jordanluyke.azores.web.api.model.Api;
import com.jordanluyke.azores.web.api.model.HttpRoute;
import com.jordanluyke.azores.web.api.routes.AudioRoutes;
import com.jordanluyke.azores.web.api.routes.SystemRoutes;
import io.netty.handler.codec.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public class ApiV1 implements Api {

    @Override
    public List<HttpRoute> getHttpRoutes() {
        return Arrays.asList(
                new HttpRoute("/status", HttpMethod.GET, SystemRoutes.GetStatus.class),
                new HttpRoute("/frequencies", HttpMethod.GET, AudioRoutes.GetFrequencies.class),
                new HttpRoute("/frequencies", HttpMethod.POST, AudioRoutes.SetFrequencies.class),
                new HttpRoute("/frequencies/start", HttpMethod.POST, AudioRoutes.StartFrequencies.class),
                new HttpRoute("/frequencies/stop", HttpMethod.POST, AudioRoutes.StopFrequencies.class)
        );
    }
}
