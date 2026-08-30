package com.post_hub.iam_service.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yaml.snakeyaml.events.Event;

@Getter
@AllArgsConstructor
public enum ApiErrorMessage {
    POST_NOT_FOUND_BY_ID("Post with ID: %s was not found"),
    POST_ALREADY_EXISTS("Post with title: %s already exists"),
    USER_NOT_FOUND_BY("User with ID: %s was not found")
    ;

    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
