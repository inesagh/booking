package com.spribe.booking.util.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AvailabilityException.class)
    public ResponseEntity<GeneralResponse> handleAvailabilityException(AvailabilityException exception) {
        LOG.error("Unhandled error: ", exception);

        return ResponseEntity
                .status(exception.getStatus())
                .body(new GeneralResponse(
                        exception.getStatus().value(),
                        exception.getMessage())
                );
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<GeneralResponse> handleAppException(AppException exception) {
        LOG.error("Unhandled error: ", exception);

        return ResponseEntity
                .status(exception.getStatus())
                .body(new GeneralResponse(
                        exception.getStatus().value(),
                        exception.getMessage())
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse> handleOtherException(Exception exception) {
        LOG.error("Unhandled error: ", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GeneralResponse(
                        500,
                        "Unexpected error occurred")
                );
    }
}
