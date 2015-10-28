package com.meltwater.puppy.http;

import com.mashape.unirest.http.exceptions.UnirestException;

public class RestClientException extends Exception {
    public RestClientException(String s, UnirestException e) {
        super(s, e);
    }

    public RestClientException(String s) {
        super(s);
    }
}
