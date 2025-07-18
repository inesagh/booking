package com.spribe.booking.util.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppException extends Exception{
    private static final Logger LOG = LoggerFactory.getLogger(AppException.class);

    public AppException(String message) {
        super(message);
        LOG.error(message);
    }

    public AppException(String message, Throwable throwable) {
        super(message, throwable);
        LOG.error(message, throwable);
    }
}
