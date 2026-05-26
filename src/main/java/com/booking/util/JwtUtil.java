package com.booking.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /*
     * Generate signing key
     */
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes()
        );
    }

    /*
     * Generate JWT token
     */
    public String generateToken(
            String username) {

        Date now = new Date();

        Date expiryDate =
                new Date(
                        now.getTime()
                                + jwtExpiration
                );

        return Jwts.builder()

                .subject(username)

                .issuedAt(now)

                .expiration(expiryDate)

                .signWith(getSigningKey())

                .compact();
    }

    /*
     * Extract username
     */
    public String getUsernameFromToken(
            String token) {

        return getClaims(token)
                .getSubject();
    }

    /*
     * Validate JWT token
     */
    public boolean validateToken(
            String token) {

        try {

            getClaims(token);

            return true;

        } catch (Exception ex) {

            return false;
        }
    }

    /*
     * Parse JWT claims
     */
    private Claims getClaims(
            String token) {

        return Jwts.parser()

                .verifyWith(getSigningKey())

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }
}