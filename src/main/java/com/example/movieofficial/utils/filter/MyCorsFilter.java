package com.example.movieofficial.utils.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MyCorsFilter extends OncePerRequestFilter {

    @Value("${url.home-page-url}")
    private String feDomain;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", feDomain);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTION");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, content-type, xsrf-token");
        response.setHeader("Access-Control-Expose-Headers", "xsrf-token");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Content-Security-Policy", "frame-ancestors " + feDomain);
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
