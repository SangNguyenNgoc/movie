package com.example.movieofficial.utils.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Setter
@Getter
public class UrlInvalidException extends AbstractException {

  private String title;

  public UrlInvalidException(String error, List<String> messages) {
    super(error, HttpStatus.BAD_REQUEST, messages);
  }

  public UrlInvalidException(String title) {
    this.title = title;
  }

}
