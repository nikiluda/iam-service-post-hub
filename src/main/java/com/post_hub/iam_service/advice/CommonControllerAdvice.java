package com.post_hub.iam_service.advice;

import com.post_hub.iam_service.model.constants.ApiConstants;
import com.post_hub.iam_service.model.exception.DataExistsException;
import com.post_hub.iam_service.model.exception.InvalidDataException;
import com.post_hub.iam_service.model.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class CommonControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<String> handleNotFoundException(
            NotFoundException exception
    ) {
        logStackTrace(exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(DataExistsException.class)
    protected ResponseEntity<String> handleDataExistException(
            DataExistsException exception
    ) {
        logStackTrace(exception);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    protected ResponseEntity<String> handleInvalidDataException(
            InvalidDataException exception
    ) {
        logStackTrace(exception);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<>();

        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            errors.put("error", error.getDefaultMessage());
        }

        return ResponseEntity
                .badRequest()
                .body(errors);
    }

    private void logStackTrace(Exception exception) {
        StringBuilder stackTrace = new StringBuilder();

        stackTrace.append(ApiConstants.ANSI_RED);

        stackTrace.append(exception.getMessage()).append(ApiConstants.BREAK_LINE);

        if (Objects.nonNull(exception.getCause())) {
            stackTrace.append(exception.getCause().getMessage()).append(ApiConstants.BREAK_LINE);
        }

        Arrays.stream(exception.getStackTrace())
                .filter(st -> st.getClassName().startsWith(ApiConstants.TIME_ZONE_PACKAGE_NAME))
                .forEach(st -> stackTrace
                        .append(st.getClassName())
                        .append(".")
                        .append(st.getMethodName())
                        .append(" (")
                        .append(st.getLineNumber())
                        .append(") ")
                );


        log.error(stackTrace.append(ApiConstants.ANSI_WHITE).toString());
    }
}
