package com.meltwater.puppy.http;

import com.google.api.client.http.HttpStatusCodes;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.RequestBodyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import static com.google.common.collect.ImmutableMap.of;

public class RabbitRestClient {

    private static final Logger log = LoggerFactory.getLogger(RabbitRestClient.class);

    private static final String PATH_VHOST_CREATE = "api/vhosts/{name}";

    private final RestRequestBuilder restRequestBuilder;
    private final Gson gson = new Gson();

    public RabbitRestClient(String brokerAddress, String brokerUsername, String brokerPassword) {
        this.restRequestBuilder = new RestRequestBuilder()
                .withHost(brokerAddress)
                .withAuthentication(brokerUsername, brokerPassword)
                .withHeader("content-type", "application/json");
    }

    public void createVirtualHost(String virtualHost, boolean tracing) throws RestClientException, UnsupportedEncodingException {
        expect(restRequestBuilder
                        .put(PATH_VHOST_CREATE)
                        .routeParam("name", escape(virtualHost))
                        .body(gson.toJson(of("tracing", tracing))),
                HttpStatusCodes.STATUS_CODE_NO_CONTENT);
    }

    private HttpResponse<JsonNode> expect(RequestBodyEntity request, int statusExpected) throws RestClientException {
        try {
            HttpResponse<JsonNode> response = request.asJson();
            if (response.getStatus() != statusExpected) {
                log.error(String.format("Response with HTTP status %d, %s", response.getStatus(), response.getStatusText()));
                throw new RestClientException(
                        String.format("Response with HTTP status %d, %s", response.getStatus(), response.getStatusText()));
            }
            return response;
        } catch (UnirestException e) {
            log.error("Unexpected error", e);
            throw new RestClientException("Unexpected error", e);
        }
    }

    private static String escape(String route) {
        return UrlEscapers.urlPathSegmentEscaper().escape(route);
    }
}
