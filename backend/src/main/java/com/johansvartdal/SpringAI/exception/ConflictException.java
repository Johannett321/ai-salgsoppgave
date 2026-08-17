package com.johansvartdal.SpringAI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "CONFLICT")
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
