package com.meltwater.puppy.config;

import java.util.HashMap;
import java.util.Map;

public class QueueData {
    private boolean durable = true;
    private boolean auto_delete = false;
    private Map<String, Object> arguments = new HashMap<>();

    public QueueData() {}

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

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "QueueData{" +
                "durable=" + durable +
                ", auto_delete=" + auto_delete +
                ", arguments=" + arguments +
                '}';
    }
}
