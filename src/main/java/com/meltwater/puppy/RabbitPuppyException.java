package com.meltwater.puppy;

import java.util.List;

/**
 * Composite exception containing all exceptions found during
 */
public class RabbitPuppyException extends Exception {
    private final List<Throwable> errors;

    public RabbitPuppyException(List<Throwable> errors) {
        this.errors = errors;
    }

    public List<Throwable> getErrors() {
        return errors;
    }
}
