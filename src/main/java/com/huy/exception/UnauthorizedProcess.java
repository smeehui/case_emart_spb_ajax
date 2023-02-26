package com.huy.exception;

public class UnauthorizedProcess extends RuntimeException {
    public UnauthorizedProcess(String message) {
        super(message);
    }
}
