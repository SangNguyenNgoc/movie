package com.example.movieofficial.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomLoginUrl extends LoginUrlAuthenticationEntryPoint {

    public CustomLoginUrl(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String redirectUri = request.getParameter("redirect_uri");
        String clientId = request.getParameter("client_id");
        String url = super.determineUrlToUseForThisRequest(request, response, exception);
        return UriComponentsBuilder.fromUriString(url)
                .queryParam("app_id", clientId)
                .queryParam("redirect_uri", URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
                .build()
                .toString();
    }
}
