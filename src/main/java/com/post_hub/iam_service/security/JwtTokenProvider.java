package com.post_hub.iam_service.security;

import com.post_hub.iam_service.model.entity.Role;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.service.model.AuthenticationConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long jwtValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long jwtValidityInMilliseconds) {

        this.secretKey = getKey(secret);

        this.jwtValidityInMilliseconds = jwtValidityInMilliseconds;
    }


    public String generateToken(@NotNull User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AuthenticationConstants.USER_ID,user.getId());
        claims.put(AuthenticationConstants.USERNAME, user.getUsername());
        claims.put(AuthenticationConstants.USER_EMAIL,user.getEmail());
        claims.put(AuthenticationConstants.USER_REGISTRATION_STATUS, user.getRegistrationStatus());
        claims.put(AuthenticationConstants.LAST_UPDATE, LocalDateTime.now().toString());

        List<String> rolesList = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        claims.put(AuthenticationConstants.ROLE, rolesList);

        return createToken(claims, user.getEmail());

    }

    private Claims getAllClaimsToken(String token) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }

    }

    public String refreshToken(String token) {
        Claims claims = getAllClaimsToken(token);
        return createToken(claims, claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmail(String token) {
        return getAllClaimsToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return getAllClaimsToken(token).get(AuthenticationConstants.ROLE,List.class);
    }

    private SecretKey getKey(String secretKey64) {
        byte[] decode64 = Decoders.BASE64.decode(secretKey64);
        return Keys.hmacShaKeyFor(decode64);
    }

    private String createToken(Map<String, Object> claims, String subjects) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subjects)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtValidityInMilliseconds))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
}