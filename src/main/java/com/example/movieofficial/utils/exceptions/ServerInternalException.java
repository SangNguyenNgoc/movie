package com.example.movieofficial.utils.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ServerInternalException extends AbstractException {
    public ServerInternalException(String error, List<String> messages) {
        super(error, HttpStatus.INTERNAL_SERVER_ERROR, messages);
    }
}
