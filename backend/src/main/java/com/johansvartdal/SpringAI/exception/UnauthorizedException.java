package com.johansvartdal.SpringAI.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "You are not authorized")
@Slf4j
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("You are not authorized");
    }

    public UnauthorizedException(String reason) {
        super(reason);
    }
}

