package com.meltwater.puppy.rest;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.util.HashMap;
import java.util.Map;

public class RestRequestBuilder {

    private String host = null;
    private String authUser = null;
    private String authPass = null;
    private String authUserNext = null;
    private String authPassNext = null;
    private Map<String, String> headers = new HashMap<>();

    public RestRequestBuilder() {
    }

    public RestRequestBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    public RestRequestBuilder withAuthentication(String authUser, String authPass) {
        this.authUser = authUser;
        this.authPass = authPass;
        return this;
    }

    public RestRequestBuilder nextWithAuthentication(String authUser, String authPass) {
        this.authUserNext = authUser;
        this.authPassNext = authPass;
        return this;
    }

    public RestRequestBuilder withHeader(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public GetRequest get(String path) {
        return addProperties(Unirest.get(hostAnd(path)));
    }

    public HttpRequestWithBody put(String path) {
        return addProperties(Unirest.put(hostAnd(path)));
    }

    public HttpRequestWithBody delete(String path) {
        return addProperties(Unirest.delete(hostAnd(path)));
    }

    public String getHost() {
        return host;
    }

    public String getAuthUser() {
        return authUser;
    }

    public String getAuthPass() {
        return authPass;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    private GetRequest addProperties(GetRequest request) {
        request.headers(this.headers);
        if (authUserNext != null && authPassNext != null) {
            request.basicAuth(authUserNext, authPassNext);
            authUserNext = null;
            authPassNext = null;
        } else if (authUser != null && authPass != null) {
            request.basicAuth(authUser, authPass);
        }
        return request;
    }

    private HttpRequestWithBody addProperties(HttpRequestWithBody request) {
        request.headers(this.headers);
        if (authUserNext != null && authPassNext != null) {
            request.basicAuth(authUserNext, authPassNext);
            authUserNext = null;
            authPassNext = null;
        } else if (authUser != null && authPass != null) {
            request.basicAuth(authUser, authPass);
        }
        return request;
    }

    private String hostAnd(String path) {
        return host == null ? path : host + path;
    }
}
