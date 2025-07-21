package com.spribe.booking.infrastructure.util.exception;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException{
    private static final Logger LOG = LoggerFactory.getLogger(AppException.class);
    private final HttpStatus status;

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        LOG.error(message);
    }
}
