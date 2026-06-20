package com.kgstrivers.trainticker.Exception;

public class ResourceNotFoundException extends TrainticketException {
    public ResourceNotFoundException(String resource, String identifier) {
        super(resource + " not found: " + identifier);
    }
}
