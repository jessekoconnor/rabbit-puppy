package com.meltwater.puppy.http;

public class RestClientException extends Exception {
    public RestClientException(String s, Exception e) {
        super(s, e);
    }

    public RestClientException(String s) {
        super(s);
    }
}
