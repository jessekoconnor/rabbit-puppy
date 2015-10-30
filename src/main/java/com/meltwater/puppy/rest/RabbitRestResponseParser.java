package com.meltwater.puppy.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.meltwater.puppy.config.ExchangeData;
import com.meltwater.puppy.config.PermissionsData;
import com.meltwater.puppy.config.UserData;
import com.meltwater.puppy.config.VHostData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class RabbitRestResponseParser {

    public Map<String, VHostData> vhosts(HttpResponse<JsonNode> response) throws RestClientException {
        try {
            JSONArray array = response.getBody().getArray();
            Map<String, VHostData> vhosts = new HashMap<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                VHostData vHostData = new VHostData(object.getBoolean("tracing"));
                vhosts.put(name, vHostData);
            }
            return vhosts;
        }
        catch (Exception e) {
            throw new RestClientException("Error parsing vhosts response", e);
        }
    }

    public Map<String, UserData> users(HttpResponse<JsonNode> response) throws RestClientException {
        try {
            JSONArray array = response.getBody().getArray();
            Map<String, UserData> users = new HashMap<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                boolean admin = object.getString("tags").contains("administrator");
                UserData userData = new UserData(null, admin);
                users.put(name, userData);
            }
            return users;
        }
        catch (Exception e) {
            throw new RestClientException("Error parsing vhosts response", e);
        }
    }

    public Map<String, PermissionsData> permissions(HttpResponse<JsonNode> response) throws RestClientException {
        try {
            JSONArray array = response.getBody().getArray();
            Map<String, PermissionsData> permissions = new HashMap<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String user = object.getString("user");
                String vhost = object.getString("vhost");
                PermissionsData permissionsData = new PermissionsData(
                        object.getString("configure"),
                        object.getString("write"),
                        object.getString("read"));
                permissions.put(user + "@" + vhost, permissionsData);
            }
            return permissions;
        }
        catch (Exception e) {
            throw new RestClientException("Error parsing vhosts response", e);
        }
    }

    public Map<String, ExchangeData> exchanges(HttpResponse<JsonNode> response) throws RestClientException {
        try {
            JSONArray array = response.getBody().getArray();
            Map<String, ExchangeData> exchanges = new HashMap<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = String.format("%s@%s", object.getString("name"), object.getString("vhost"));
                ExchangeData exchangeData = new ExchangeData(
                        object.getString("type"),
                        object.getBoolean("durable"),
                        object.getBoolean("auto_delete"),
                        object.getBoolean("internal"),
                        toMap(object.getJSONObject("arguments")));
                exchanges.put(name, exchangeData);
            }
            return exchanges;
        }
        catch (Exception e) {
            throw new RestClientException("Error parsing vhosts response", e);
        }
    }


    private Map<String, Object> toMap(final JSONObject object) {
        return ((Set<Object>) object.keySet()).stream()
                .map(String::valueOf)
                .collect(Collectors.toMap(
                        k -> k,
                        object::get
                ));
    }
}
