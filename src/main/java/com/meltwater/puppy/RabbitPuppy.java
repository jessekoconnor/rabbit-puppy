package com.meltwater.puppy;

import com.meltwater.puppy.config.ExchangeData;
import com.meltwater.puppy.config.PermissionsData;
import com.meltwater.puppy.config.RabbitConfig;
import com.meltwater.puppy.config.UserData;
import com.meltwater.puppy.config.VHostData;
import com.meltwater.puppy.config.reader.RabbitConfigException;
import com.meltwater.puppy.rest.RabbitRestClient;
import com.meltwater.puppy.rest.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class RabbitPuppy {

    private static final Logger log = LoggerFactory.getLogger(RabbitPuppy.class);

    private final Pattern permissionsPattern = Pattern.compile("([^@]+)@([^@]+)");
    private final Pattern exchangePattern = Pattern.compile("([^@]+)@([^@]+)");
    private final Pattern queuePattern = Pattern.compile("([^@]+)@([^@]+)");
    private final Pattern bindingPattern = Pattern.compile("([^@]+)@([^@]+)@([^@]+)");

    private final RabbitRestClient client;

    public RabbitPuppy(String brokerAddress, String username, String password) {
        client = new RabbitRestClient(brokerAddress, username, password);
    }

    public RabbitPuppy(RabbitRestClient client) {
        this.client = client;
    }

    /**
     * Apply configuration to RabbitMQ Broker
     * @param config Configuration to apply
     * @throws RabbitPuppyException If errors or configuration mismatches are encountered
     */
    public void apply(RabbitConfig config) throws RabbitPuppyException {
        List<Throwable> errors = new ArrayList<>();

        if (config.getVhosts().size() > 0)
            errors.addAll(createVHosts(config.getVhosts()));

        if (config.getUsers().size() > 0)
            errors.addAll(createUsers(config.getUsers()));

        if (config.getPermissions().size() > 0)
            errors.addAll(createPermissions(config.getPermissions()));

        if (config.getExchanges().size() > 0)
            errors.addAll(createExchanges(config));

        if (errors.size() > 0) {
            throw new RabbitPuppyException("Encountered errors while applying configuration", errors);
        }
    }

    /**
     * Create vhosts based on configuration.
     * @param vhosts Configured vhosts
     * @return List of errors encountered during creation
     */
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
                client.createVirtualHost(entry.getKey(), data);
            });
        });
        return errors;
    }

    /**
     * Create users based on configuration.
     * @param users Configured users
     * @return List of errors encountered during creation
     */
    private List<Throwable> createUsers(Map<String, UserData> users) {
        final List<Throwable> errors = new ArrayList<>();
        final Map<String, UserData> existing;

        try {
            existing = withKnownPasswords(client.getUsers(), users);
        } catch (RestClientException e) {
            log.error("Failed to fetch vhosts", e);
            errors.add(e);
            return errors;
        }

        users.entrySet().forEach(entry -> {
            String name = entry.getKey();
            UserData data = entry.getValue() == null ? new UserData() : entry.getValue();
            ensurePresent("user", name, data, existing, errors, () -> {
                log.info("Creating user " + entry.getKey());
                client.createUser(entry.getKey(), data);
            });
        });
        return errors;
    }

    /**
     * Creates user permissions per vhost based on configuration.
     * @param permissions Configured permissions
     * @return List of errors encountered during creation
     */
    private List<Throwable> createPermissions(Map<String, PermissionsData> permissions) {
        final List<Throwable> errors = new ArrayList<>();
        final Map<String, PermissionsData> existing;

        try {
            existing = client.getPermissions();
        } catch (RestClientException e) {
            log.error("Failed to fetch exchanges", e);
            errors.add(e);
            return errors;
        }

        permissions.entrySet().forEach(entry -> {
            String name = entry.getKey();
            final Matcher matcher = permissionsPattern.matcher(name);
            if (matcher.matches()) {
                PermissionsData data = entry.getValue() == null ? new PermissionsData() : entry.getValue();
                ensurePresent("permissions", name, data, existing, errors, () -> {
                    String user = matcher.group(1);
                    String vhost = matcher.group(2);
                    log.info(format("Setting permissions for user %s at vhost %s", user, vhost));
                    client.createPermissions(user, vhost, data);
                });
            } else {
                String error = format("Invalid exchange format '%s', should be exchange@vhost", name);
                log.error(error);
                errors.add(new RabbitConfigException(error));
            }
        });
        return errors;
    }

    /**
     * Creates exchanges based on configuration.
     * @param config Rabbit configuration
     * @return List of errors encountered during creation
     */
    private List<Throwable> createExchanges(RabbitConfig config) {
        final List<Throwable> errors = new ArrayList<>();
        final Map<String, ExchangeData> existing;

        try {
            existing = client.getExchanges();
        } catch (RestClientException e) {
            log.error("Failed to fetch exchanges", e);
            errors.add(e);
            return errors;
        }

        config.getExchanges().entrySet().forEach(entry -> {
            String name = entry.getKey();
            final Matcher matcher = exchangePattern.matcher(name);
            if (matcher.matches()) {
                ExchangeData data = entry.getValue() == null ? new ExchangeData() : entry.getValue();
                ensurePresent("exchange", name, data, existing, errors, () -> {
                    String exchange = matcher.group(1);
                    String vhost = matcher.group(2);
                    log.info(format("Creating exchange %s at vhost %s", exchange, vhost));
                    final Optional<String> user = findPermissibleUserForCreate(config.getPermissions(), vhost, exchange);
                    if (user.isPresent()) {
                        client.createExchange(exchange, vhost, data,
                                user.get(), config.getUsers().get(user.get()).getPassword());
                    } else {
                        client.createExchange(exchange, vhost, data);
                    }
                });
            } else {
                String error = format("Invalid exchange format '%s', should be exchange@vhost", name);
                log.error(error);
                errors.add(new RabbitConfigException(error));
            }
        });
        return errors;
    }


    private void waitUntilBrokerAvailable() { // TODO A flag

    }

    /**
     * Ensures that the configured resources are present on the broker.
     * Throws exception if creation failed, or resource exists with settings that does not match expected configuration.
     */
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

    /**
     * Because lambdas.
     */
    private interface Create {
        void create() throws RestClientException;
    }

    /**
     * Copies known passwords onto users received from broker, since we get only password hash from it, so that
     * ensurePresent does not fail due to differences in the password field.
     * @param existing   Existing users
     * @param fromConfig Users from input configuration
     * @return           Existing users with known passwords appended
     */
    private Map<String, UserData> withKnownPasswords(Map<String, UserData> existing,
                                                     Map<String, UserData> fromConfig) {
        existing.forEach((user, data) -> {
            if (fromConfig.containsKey(user)) {
                data.setPassword(fromConfig.get(user).getPassword());
            }
        });
        return existing;
    }

    /**
     * Attempts to find a user in the given configuration with rights to create the requested resource.
     *
     * @param permissions  user permissions
     * @param vhost        vhost name
     * @param resourceName resource name
     * @return Optional of user with creation rights, or Optional.empty() if not found.
     */
    // TODO Test
    private Optional<String> findPermissibleUserForCreate(Map<String, PermissionsData> permissions,
                                                          String vhost,
                                                          String resourceName) {
        return permissions.entrySet().stream()
                .filter(entry -> {
                    final Matcher matcher = permissionsPattern.matcher(entry.getKey());
                    if (matcher.matches() && matcher.group(2).equals(vhost)) {
                        if (Pattern.compile(entry.getValue().getConfigure()).matcher(resourceName).matches()) {
                            return true;
                        }
                    }
                    return false;
                })
                .map(entry -> {
                    Matcher matcher = permissionsPattern.matcher(entry.getKey());
                    matcher.find(); // Causes matcher to actually parse regex groups
                    return matcher.group(1);
                })
                .findFirst();
    }
}
