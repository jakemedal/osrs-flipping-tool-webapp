package com.osrs.fliptool.service.exception;

public class ApiUrlConnectionException extends RuntimeException {
    public ApiUrlConnectionException(String msg) {
        super(msg);
    }

    public ApiUrlConnectionException(String msg, Exception e) {
        super(msg, e);
    }
}
