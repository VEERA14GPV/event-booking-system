package com.booking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import java.util.Date;

@Component
public class JwtTokenProvider {

	@Value("${app.jwt.secret}")
	private String jwtSecret;

	@Value("${app.jwt.expiration}")
	private long jwtExpirationDate;

    /*
     * Generate signing key
     */
	
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(

                jwtSecret.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }

    /*
     * Generate JWT token
     */
    
    public String generateToken(
            Authentication authentication) {

        String username =
                authentication.getName();

        Date currentDate =
                new Date();

        Date expireDate =
                new Date(

                        currentDate.getTime()
                                + jwtExpirationDate
                );

        return Jwts.builder()

                .subject(username)

                .issuedAt(currentDate)

                .expiration(expireDate)

                .signWith(
                        getSigningKey()
                )

                .compact();
    }

    /*
     * Extract username
     */
    
    public String getUsernameFromToken(
            String token) {

        Claims claims = Jwts.parser()

                .verifyWith(
                        getSigningKey()
                )

                .build()

                .parseSignedClaims(token)

                .getPayload();

        return claims.getSubject();
    }

    /*
     * Validate JWT token
     */
    
    public boolean validateToken(
            String token) {

        try {

            Jwts.parser()

                    .verifyWith(
                            getSigningKey()
                    )

                    .build()

                    .parseSignedClaims(token);

            return true;

        } catch (Exception ex) {

            return false;
        }
    }
}