package com.johansvartdal.SpringAI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "Bad request")
public class InsufficientQuotaException extends RuntimeException{

    public InsufficientQuotaException() {

    }

    public InsufficientQuotaException(String message) {
        super(message);
    }
}
