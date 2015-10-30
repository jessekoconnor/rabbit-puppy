package com.meltwater.puppy.rest;

import com.google.common.net.UrlEscapers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.body.RequestBodyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestUtils {

    private static final Logger log = LoggerFactory.getLogger(RestUtils.class);

    public static String escape(String route) {
        return UrlEscapers.urlPathSegmentEscaper().escape(route);
    }

    public static HttpResponse<JsonNode> expect(GetRequest request, int statusExpected) throws RestClientException {
        try {
            HttpResponse<JsonNode> response = request.asJson();
            if (response.getStatus() != statusExpected) {
                String error = String.format("Response with HTTP status %d %s, expected status code %d",
                        response.getStatus(), response.getStatusText(), statusExpected);
                log.error(error);
                throw new RestClientException(error);
            }
            return response;
        } catch (UnirestException e) {
            log.error("Unexpected error", e);
            throw new RestClientException("Unexpected error", e);
        }
    }

    public static HttpResponse<JsonNode> expect(RequestBodyEntity request, int statusExpected) throws RestClientException {
        try {
            HttpResponse<JsonNode> response = request.asJson();
            if (response.getStatus() != statusExpected) {
                String error = String.format("Response with HTTP status %d %s, expected status code %d",
                        response.getStatus(), response.getStatusText(), statusExpected);
                log.error(error);
                throw new RestClientException(error);
            }
            return response;
        } catch (UnirestException e) {
            log.error("Unexpected error", e);
            throw new RestClientException("Unexpected error", e);
        }
    }
}
