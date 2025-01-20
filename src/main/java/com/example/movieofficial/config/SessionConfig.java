package com.example.movieofficial.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Log4j2
public class SessionConfig implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        Duration timeout = Duration.ofSeconds(60);
        log.info("Session created: {}", se.getSession().getId());
        se.getSession().setMaxInactiveInterval((int) timeout.toSeconds());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // No action needed
    }
}
