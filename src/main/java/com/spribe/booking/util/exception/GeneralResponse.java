package com.spribe.booking.util.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
