package com.spribe.booking.infrastructure.util.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;


public class AvailabilityException extends AppException{
    private static final Logger LOG = LoggerFactory.getLogger(AvailabilityException.class);

    public AvailabilityException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
        LOG.error(message);
    }
}
