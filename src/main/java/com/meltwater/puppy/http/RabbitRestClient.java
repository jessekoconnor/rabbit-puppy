package com.meltwater.puppy.http;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import com.meltwater.puppy.config.VHostData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static com.meltwater.puppy.http.RestUtils.escape;
import static com.meltwater.puppy.http.RestUtils.expect;

public class RabbitRestClient {

    private static final Logger log = LoggerFactory.getLogger(RabbitRestClient.class);

    private static final String PATH_VHOSTS = "api/vhosts";
    private static final String PATH_VHOSTS_CREATE = "api/vhosts/{name}";

    private final RestRequestBuilder restRequestBuilder;
    private final RabbitRestResponseParser parser = new RabbitRestResponseParser();
    private final Gson gson = new Gson();

    public RabbitRestClient(String brokerAddress, String brokerUsername, String brokerPassword) {
        this.restRequestBuilder = new RestRequestBuilder()
                .withHost(brokerAddress)
                .withAuthentication(brokerUsername, brokerPassword)
                .withHeader("content-type", "application/json");
    }

    public void createVirtualHost(String virtualHost, boolean tracing) throws RestClientException {
        expect(restRequestBuilder
                        .put(PATH_VHOSTS_CREATE)
                        .routeParam("name", escape(virtualHost))
                        .body(gson.toJson(of("tracing", tracing))),
                HttpStatusCodes.STATUS_CODE_NO_CONTENT);
    }

    public Map<String, VHostData> getVirtualHosts() throws RestClientException {
        return parser.vhostsAll(
                expect(restRequestBuilder
                                .get(PATH_VHOSTS),
                        HttpStatusCodes.STATUS_CODE_OK));
    }
}
