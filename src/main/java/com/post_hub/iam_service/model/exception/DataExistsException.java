package com.post_hub.iam_service.model.exception;

public class DataExistsException extends RuntimeException {
    public DataExistsException(String message) {
        super(message);
    }
}
