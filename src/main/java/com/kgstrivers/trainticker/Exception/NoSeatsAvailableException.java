package com.kgstrivers.trainticker.Exception;

public class NoSeatsAvailableException extends TrainticketException {
    public NoSeatsAvailableException(String message) {
        super(message);
    }
}