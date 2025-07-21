package com.spribe.booking.infrastructure.util.exception;

import lombok.Data;

@Data
public class GeneralResponse {
    private int statusCode;
    private String message;

    public GeneralResponse(String message) {
        this.message = message;
    }

    public GeneralResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
