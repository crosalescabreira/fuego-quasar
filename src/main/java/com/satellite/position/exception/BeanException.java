package com.satellite.position.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeanException  extends RuntimeException {
    public BeanException(String message) {
        super(message);
    }
}