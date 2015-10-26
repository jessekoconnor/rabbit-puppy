package com.meltwater.puppy.config;


import java.util.Map;

public class ExchangeData {

    private String type;
    private boolean durable = true;
    private boolean auto_delete = false;
    private boolean internal = false;
    private Map<String, Object> arguments;

    public ExchangeData() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isAuto_delete() {
        return auto_delete;
    }

    public void setAuto_delete(boolean auto_delete) {
        this.auto_delete = auto_delete;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "ExchangeData{" +
                "type='" + type + '\'' +
                ", durable=" + durable +
                ", auto_delete=" + auto_delete +
                ", internal=" + internal +
                ", arguments=" + arguments +
                '}';
    }
}
