package com.ecore.roles.exception;

import static java.lang.String.format;

public class ForbiddenException extends RuntimeException {

    public <T> ForbiddenException(Class<T> resource, String message) {
        super(format("Invalid '%s' object. %s", resource.getSimpleName(), message));
    }
}
