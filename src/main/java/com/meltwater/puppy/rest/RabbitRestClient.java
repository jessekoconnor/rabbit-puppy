package com.meltwater.puppy.rest;

import com.google.api.client.http.HttpStatusCodes;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.meltwater.puppy.config.BindingData;
import com.meltwater.puppy.config.ExchangeData;
import com.meltwater.puppy.config.PermissionsData;
import com.meltwater.puppy.config.QueueData;
import com.meltwater.puppy.config.UserData;
import com.meltwater.puppy.config.VHostData;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Maps.newHashMap;
import static com.meltwater.puppy.rest.RestUtils.escape;
import static com.meltwater.puppy.rest.RestUtils.expect;
import static java.lang.String.format;

public class RabbitRestClient {

    public static final String PATH_VHOSTS = "api/vhosts";
    public static final String PATH_VHOSTS_CREATE = "api/vhosts/{vhost}";
    public static final String PATH_USERS = "api/users";
    public static final String PATH_USERS_CREATE = "api/users/{user}";
    public static final String PATH_PERMISSIONS = "api/permissions";
    public static final String PATH_PERMISSIONS_CREATE = "api/permissions/{vhost}/{user}";
    public static final String PATH_EXCHANGES = "api/exchanges";
    public static final String PATH_EXCHANGES_CREATE = "api/exchanges/{vhost}/{exchange}";
    public static final String PATH_QUEUES = "api/exchanges";
    public static final String PATH_QUEUES_CREATE = "api/exchanges/{vhost}/{queue}";

    private final RestRequestBuilder requestBuilder;
    private final RabbitRestResponseParser parser = new RabbitRestResponseParser();
    private final Gson gson = new Gson();

    public RabbitRestClient(String brokerAddress, String brokerUsername, String brokerPassword) {
        this.requestBuilder = new RestRequestBuilder()
                .withHost(brokerAddress)
                .withAuthentication(brokerUsername, brokerPassword)
                .withHeader("content-type", "application/json");
    }

    public void createVirtualHost(String virtualHost, VHostData vHostData) throws RestClientException {
        expect(requestBuilder
                        .put(PATH_VHOSTS_CREATE)
                        .routeParam("vhost", escape(virtualHost))
                        .body(gson.toJson(vHostData)),
                HttpStatusCodes.STATUS_CODE_NO_CONTENT);
    }

    public Map<String, VHostData> getVirtualHosts() throws RestClientException {
        return parser.vhosts(
                expect(requestBuilder
                                .get(PATH_VHOSTS),
                        HttpStatusCodes.STATUS_CODE_OK));
    }

    public void createUser(String user, UserData userData) throws RestClientException {
        if (userData.getPassword() == null)
            throw new RestClientException(format("User %s missing required field: password", user));
        expect(requestBuilder
                        .put(PATH_USERS_CREATE)
                        .routeParam("user", escape(user))
                        .body(gson.toJson(of(
                                "password", userData.getPassword(),
                                "tags", userData.isAdmin() ? "administrator" : ""
                        ))),
                HttpStatusCodes.STATUS_CODE_NO_CONTENT);
    }

    public Map<String, UserData> getUsers() throws RestClientException {
        return parser.users(
                expect(requestBuilder
                                .get(PATH_USERS),
                        HttpStatusCodes.STATUS_CODE_OK));
    }

    public void createPermissions(String user, String vhost, PermissionsData permissionsData) throws RestClientException {
        expect(requestBuilder
                        .put(PATH_PERMISSIONS_CREATE)
                        .routeParam("vhost", escape(vhost))
                        .routeParam("user", escape(user))
                        .body(gson.toJson(permissionsData)),
                HttpStatusCodes.STATUS_CODE_NO_CONTENT);
    }

    public Map<String, PermissionsData> getPermissions() throws RestClientException {
        return parser.permissions(
                expect(requestBuilder
                                .get(PATH_PERMISSIONS),
                        HttpStatusCodes.STATUS_CODE_OK));
    }

    public void createExchange(String exchange, String vhost, ExchangeData exchangeData) throws RestClientException {
        createExchange(exchange, vhost, exchangeData, requestBuilder.getAuthUser(), requestBuilder.getAuthPass());
    }

    public void createExchange(String exchange, String vhost, ExchangeData exchangeData, String user, String pass) throws RestClientException {
        if (exchangeData.getType() == null)
            throw new RestClientException(format("Exchange %s@%s missing required field: type", exchange, vhost));
        expect(requestBuilder
                        .nextWithAuthentication(user, pass)
                        .put(PATH_EXCHANGES_CREATE)
                        .routeParam("vhost", escape(vhost))
                        .routeParam("exchange", escape(exchange))
                        .body(gson.toJson(exchangeData)),
                HttpStatusCodes.STATUS_CODE_NO_CONTENT);
    }

    public Map<String, ExchangeData> getExchanges() throws RestClientException {
        return parser.exchanges(
                expect(requestBuilder
                                .get(PATH_EXCHANGES),
                        HttpStatusCodes.STATUS_CODE_OK));
    }

    public void createQueue(String queue, String vhost, QueueData queueData, String user, String pass) throws RestClientException {

    }

    public Map<String, QueueData> getQueues() throws RestClientException {
        return null;
    }

    public void createBinding(String from, String to, String vhost, BindingData bindingData, String user, String pass) throws RestClientException {

    }

    public Map<String, BindingData> getBindings() throws RestClientException {
        return null;
    }
}
