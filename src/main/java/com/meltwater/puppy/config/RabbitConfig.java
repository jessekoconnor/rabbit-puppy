package com.meltwater.puppy.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RabbitConfig {
    private List<String> vhosts = new ArrayList<>();
    private Map<String, UserData> users = new HashMap<>();
    private Map<String, PermissionsData> permissions = new HashMap<>();
    private Map<String, ExchangeData> exchanges = new HashMap<>();
    private Map<String, QueueData> queues = new HashMap<>();
    private Map<String, BindingData> bindings= new HashMap<>();

    public RabbitConfig() {}

    public List<String> getVhosts() {
        return vhosts;
    }

    public void setVhosts(List<String> vhosts) {
        this.vhosts = vhosts;
    }

    public Map<String, UserData> getUsers() {
        return users;
    }

    public void setUsers(Map<String, UserData> users) {
        this.users = users;
    }

    public Map<String, PermissionsData> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, PermissionsData> permissions) {
        this.permissions = permissions;
    }

    public Map<String, ExchangeData> getExchanges() {
        return exchanges;
    }

    public void setExchanges(Map<String, ExchangeData> exchanges) {
        this.exchanges = exchanges;
    }

    public Map<String, QueueData> getQueues() {
        return queues;
    }

    public void setQueues(Map<String, QueueData> queues) {
        this.queues = queues;
    }

    public Map<String, BindingData> getBindings() {
        return bindings;
    }

    public void setBindings(Map<String, BindingData> bindings) {
        this.bindings = bindings;
    }

    @Override
    public String toString() {
        return "RabbitConfig{" +
                "vhosts=" + vhosts +
                ", users=" + users +
                ", permissions=" + permissions +
                ", exchanges=" + exchanges +
                ", queues=" + queues +
                ", bindings=" + bindings +
                '}';
    }
}
