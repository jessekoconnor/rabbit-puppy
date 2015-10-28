package com.meltwater.puppy.config;

public class BindingData {
    private String destination;
    private String routing_key;

    public BindingData() {}

    public BindingData(String destination, String routing_key) {
        this.destination = destination;
        this.routing_key = routing_key;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BindingData that = (BindingData) o;

        if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
        return !(routing_key != null ? !routing_key.equals(that.routing_key) : that.routing_key != null);

    }

    @Override
    public int hashCode() {
        int result = destination != null ? destination.hashCode() : 0;
        result = 31 * result + (routing_key != null ? routing_key.hashCode() : 0);
        return result;
    }
}
