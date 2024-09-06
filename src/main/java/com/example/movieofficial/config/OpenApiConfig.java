package com.example.movieofficial.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Sang Nguyễn",
                        email = "nngocsang38@gmail.com",
                        url = "https://github.com/SangNguyenNgoc"
                ),
                description = "Open Api for booking movie app",
                title = "OpenApi booking movie - Sang Nguyễn",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "api.pwer-dev.id.vn",
                        url = "${customBaseUrl}"
                )
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = "JWT auth description",
        type = SecuritySchemeType.OAUTH2,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "${customBaseUrl}/oauth2/authorize",
                        tokenUrl = "${customBaseUrl}/oauth2/token",
                        scopes = {
                                @OAuthScope(name = "openid", description = "openid"),
                        },
                        extensions = {
                                @Extension(name = "x-pkce", properties = {
                                        @ExtensionProperty(name = "required", value = "true")
                                })
                        }
                )
        )
)
public class OpenApiConfig {
    @Value("${url.base-url}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        System.setProperty("customBaseUrl", baseUrl);
    }
}

