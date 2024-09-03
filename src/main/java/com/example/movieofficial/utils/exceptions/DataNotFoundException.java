package com.example.movieofficial.utils.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class DataNotFoundException extends AbstractException {
    public DataNotFoundException(String error, List<String> messages) {
        super(error, HttpStatus.NOT_FOUND, messages);
    }
}
