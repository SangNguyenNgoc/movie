package com.example.movieofficial.utils.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ForbiddenException extends AbstractException {
    public ForbiddenException(String error, List<String> messages) {
        super(error, HttpStatus.FORBIDDEN, messages);
    }
}
