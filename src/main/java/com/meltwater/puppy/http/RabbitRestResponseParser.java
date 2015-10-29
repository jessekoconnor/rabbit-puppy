package com.meltwater.puppy.http;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.meltwater.puppy.config.VHostData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RabbitRestResponseParser {

    public Map<String, VHostData> vhostsAll(HttpResponse<JsonNode> response) throws RestClientException {
        try {
            JSONArray array = response.getBody().getArray();
            Map<String, VHostData> vhosts = new HashMap<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                vhosts.put(object.getString("name"), new VHostData(object.getBoolean("tracing")));
            }
            return vhosts;
        }
        catch (Exception e) {
            throw new RestClientException("Error parsing vhosts response", e);
        }
    }
}
