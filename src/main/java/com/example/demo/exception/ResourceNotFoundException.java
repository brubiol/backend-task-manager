package com.example.demo.exception;

/**
 * Custom exception for "resource not found" scenarios.
 *
 * This maps to HTTP 404 in our global exception handler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with id: %d", resourceName, id));
    }
}
