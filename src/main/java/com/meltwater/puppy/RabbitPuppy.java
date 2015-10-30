package com.meltwater.puppy;

import com.meltwater.puppy.config.RabbitConfig;
import com.meltwater.puppy.config.VHostData;
import com.meltwater.puppy.http.RabbitRestClient;
import com.meltwater.puppy.http.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class RabbitPuppy {

    private static final Logger log = LoggerFactory.getLogger(RabbitPuppy.class);

    private final RabbitRestClient client;

    public RabbitPuppy(String brokerAddress, String username, String password) {
        client = new RabbitRestClient(brokerAddress, username, password);
    }

    public RabbitPuppy(RabbitRestClient client) {
        this.client = client;
    }

    public void apply(RabbitConfig rabbitConfig) throws RabbitPuppyException {
        List<Throwable> errors = createVHosts(rabbitConfig.getVhosts());
        if (errors.size() > 0) {
            throw new RabbitPuppyException(errors);
        }
    }

    private List<Throwable> createVHosts(Map<String, VHostData> vhosts) {
        final List<Throwable> errors = new ArrayList<>();
        final Map<String, VHostData> existing;

        try {
            existing = client.getVirtualHosts();
        } catch (RestClientException e) {
            log.error("Failed to fetch vhosts", e);
            errors.add(e);
            return errors;
        }

        vhosts.entrySet().forEach(entry -> {
            String name = entry.getKey();
            VHostData data = entry.getValue() == null ? new VHostData() : entry.getValue();
            ensurePresent("vhost", name, data, existing, errors, () -> {
                log.info("Creating vhost " + entry.getKey());
                boolean tracing = entry.getValue() != null && entry.getValue().isTracing();
                client.createVirtualHost(entry.getKey(), tracing);
            });
        });
        return errors;
    }

    private void waitUntilBrokerAvailable() { // TODO A flag

    }

    private <D> void ensurePresent(String type, String name, D data, Map<String, D> existing, List<Throwable> errors, Create create) {
        if (existing.containsKey(name)) {
            if (!existing.get(name).equals(data)) {
                String error = format("%s '%s' exists but with wrong configuration: %s, expected: %s",
                        type, name, existing.get(name), data);
                log.error(error);
                errors.add(new InvalidConfigurationException(error));
            }
        } else {
            try {
                create.create();
            } catch (RestClientException e) {
                log.error(format("Failed to create %s '%s': %s", type, name, e.getMessage()));
                errors.add(e);
            }
        }
    }

    private interface Create {
        void create() throws RestClientException;
    }
}
