package com.meltwater.puppy.config;

public class BindingData {
    private String destination;
    private String routing_key;

    public BindingData() {}

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getRouting_key() {
        return routing_key;
    }

    public void setRouting_key(String routing_key) {
        this.routing_key = routing_key;
    }

    @Override
    public String toString() {
        return "BindingData{" +
                "destination='" + destination + '\'' +
                ", routing_key='" + routing_key + '\'' +
                '}';
    }
}
