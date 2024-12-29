package com.example.movieofficial.utils.services;

import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    public String generateVerifyToken(User user, Long timeout) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(timeout, ChronoUnit.MINUTES))
                .subject(user.getUsername())
                .claim("scope", List.of("VERIFY"))
                .notBefore(Instant.ofEpochSecond(now.getEpochSecond() + 60))
                .build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            Instant expiresAt = decodedJwt.getExpiresAt();
            assert expiresAt != null;
            return expiresAt.isBefore(Instant.now());
        } catch (JwtException e) {
            return true;
        }
    }

    public String extractSubject(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public long extractExpInSeconds(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        Instant exp = jwt.getClaimAsInstant("exp");
        return exp != null ? exp.getEpochSecond() : 0;
    }

    public String extractId(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getId();
    }

    public String extractClaim(String claim, String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim(claim);
    }

    public String validateTokenBearer(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("Unauthorized", List.of("Unauthorized"));
        } else {
            token = token.substring(7);
            return token;
        }
    }

    public String getRandomNumber(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder uid = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            uid.append(digit);
        }
        return uid.toString();
    }
}
