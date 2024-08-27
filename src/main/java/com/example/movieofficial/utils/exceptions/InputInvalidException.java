package com.example.movieofficial.utils.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class InputInvalidException extends AbstractException{

    public InputInvalidException(String error, List<String> messages) {
        super(error, HttpStatus.BAD_REQUEST, messages);
    }
}
