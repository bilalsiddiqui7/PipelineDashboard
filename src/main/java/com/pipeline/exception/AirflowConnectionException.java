package com.pipeline.exception;

public class AirflowConnectionException extends RuntimeException {

    public AirflowConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
