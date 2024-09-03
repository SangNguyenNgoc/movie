package com.example.movieofficial.utils.exceptions;

import com.example.movieofficial.api.user.exceptions.RoleNotFoundException;
import com.example.movieofficial.api.user.exceptions.UnauthorizedException;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException e) {
        return buildResponse(e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleException(UnauthorizedException e) {
        return buildResponse(e);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(RoleNotFoundException e) {
        return buildResponse(e);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(DataNotFoundException e) {
        return buildResponse(e);
    }

    @ExceptionHandler(InputInvalidException.class)
    public ResponseEntity<ErrorResponse> handleException(InputInvalidException e) {
        return buildResponse(e);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleException(AppException e) {
        return buildResponse(e);
    }

    public ResponseEntity<ErrorResponse> buildResponse(AbstractException e) {
        return ResponseEntity.status(e.getStatus().value()).body(
                ErrorResponse.builder()
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .httpStatus(e.getStatus())
                        .statusCode(e.getStatus().value())
                        .error(e.getError())
                        .messages(e.getMessages())
                        .build()
        );
    }

}
