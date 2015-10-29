package com.meltwater.puppy.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RabbitConfig {
    private Map<String, VHostData> vhosts = new HashMap<>();
    private Map<String, UserData> users = new HashMap<>();
    private Map<String, PermissionsData> permissions = new HashMap<>();
    private Map<String, ExchangeData> exchanges = new HashMap<>();
    private Map<String, QueueData> queues = new HashMap<>();
    private Map<String, BindingData> bindings= new HashMap<>();

    public RabbitConfig() {}

    public Map<String, VHostData> getVhosts() {
        return vhosts;
    }

    public void setVhosts(Map<String, VHostData> vhosts) {
        this.vhosts = vhosts;
    }

    public RabbitConfig addVhost(String name, VHostData vHostData) {
        vhosts.put(name, vHostData);
        return this;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RabbitConfig that = (RabbitConfig) o;

        if (vhosts != null ? !vhosts.equals(that.vhosts) : that.vhosts != null) return false;
        if (users != null ? !users.equals(that.users) : that.users != null) return false;
        if (permissions != null ? !permissions.equals(that.permissions) : that.permissions != null) return false;
        if (exchanges != null ? !exchanges.equals(that.exchanges) : that.exchanges != null) return false;
        if (queues != null ? !queues.equals(that.queues) : that.queues != null) return false;
        return !(bindings != null ? !bindings.equals(that.bindings) : that.bindings != null);

    }

    @Override
    public int hashCode() {
        int result = vhosts != null ? vhosts.hashCode() : 0;
        result = 31 * result + (users != null ? users.hashCode() : 0);
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        result = 31 * result + (exchanges != null ? exchanges.hashCode() : 0);
        result = 31 * result + (queues != null ? queues.hashCode() : 0);
        result = 31 * result + (bindings != null ? bindings.hashCode() : 0);
        return result;
    }
}
