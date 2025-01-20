package com.example.movieofficial.config;

import com.example.movieofficial.api.user.auth.CustomAuthenticationFailureHandler;
import com.example.movieofficial.utils.auditing.ApplicationAuditAware;
import com.example.movieofficial.utils.filter.BlackListFilter;
import com.example.movieofficial.utils.filter.MyCorsFilter;
import com.example.movieofficial.utils.services.RedisService;
import com.example.movieofficial.utils.services.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.time.Instant;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final MyCorsFilter myCorsFilter;
    private final RedisService<String> redisService;
    private final TokenService tokenService;

    @Value("${url.login-page-url}")
    private String loginPageUrl;

    @Value("${url.login-url}")
    private String loginUrl;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults())
                .and()
                .addFilterBefore(myCorsFilter, ChannelProcessingFilter.class)
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new CustomLoginUrl(loginPageUrl),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                .oauth2ResourceServer(resource -> resource.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .oauth2ResourceServer(resource -> resource.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/css/**",
                                "/fonts/**",
                                "/images/**",
                                "/js/**",
                                "/error"
                        )
                        .permitAll()
                        .anyRequest().permitAll())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .userDetailsService(userDetailsService)
                .addFilterBefore(myCorsFilter, ChannelProcessingFilter.class)
                .formLogin(login -> login
                        .loginPage(loginPageUrl)
                        .loginProcessingUrl(loginUrl)
                        .permitAll());
        httpSecurity.logout(logout -> logout
                .logoutUrl("/logout") // URL để xử lý yêu cầu logout
                .logoutSuccessHandler((request, response, authentication) -> {
                    String token = request.getHeader("Authorization");
                    request.getSession().invalidate();
                    Cookie cookie = new Cookie("JSESSIONID", null);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    long expTimestamp = tokenService.extractExpInSeconds(token.substring(7));
                    long currentTimestamp = Instant.now().getEpochSecond();
                    long secondsRemaining = expTimestamp - currentTimestamp;
                    long minutesRemaining = secondsRemaining / 60;
                    String idToken = tokenService.extractId(token.substring(7));
                    redisService.setValue("black_list:" + idToken, "", minutesRemaining);
                })
                .invalidateHttpSession(true)  // Hủy session hiện tại
                .clearAuthentication(true) // Xóa thông tin xác thực
        );
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authConverter;
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new ApplicationAuditAware();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}
