package com.backend.ems.Util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.backend.ems.Exception.CustomJWTException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JWTUtils {

    private SecretKey jwtSecret;
    private SecretKey refreshJwtSecret;
    private static final long EXPIRATION_TIME = 2 * 60 * 1000;// 24 * 60 * 60 * 1000

    public JWTUtils() {
        String jwtSecretBase64 = "c3VwZXIta2V5LXZhbHVlcy1mb3ItamF2YS1sYWJzbC1jYXNl";
        String refreshJwtSecretBase64 = "c3VwZXIta2V5LXZhbHVlcy1mb3ItamF2YS1sYWJzbC1jYXNl";

        byte[] keyBytes = Base64.getDecoder().decode(jwtSecretBase64);
        this.jwtSecret = new SecretKeySpec(keyBytes, "HmacSHA256");

        byte[] keyBytes2 = Base64.getDecoder().decode(refreshJwtSecretBase64);
        this.refreshJwtSecret = new SecretKeySpec(keyBytes2, "HmacSHA256");
    }

    // private Key key() {
    // return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    // }

    // private Key keyRefresh() {
    // return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshJwtSecret));
    // }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))// 24 hour
                .signWith(jwtSecret)
                .compact();
    }

    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000))// 15 days 1209600000
                .signWith(refreshJwtSecret)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return extractClaims(token, Claims::getSubject);
        } catch (Exception e) {
            throw new CustomJWTException("Invalid Token.");
        }
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction
                .apply(Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(token).getPayload());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            throw new CustomJWTException("Token Expired");
        }
    }

    private <T> T extractClaimsForRefreshToken(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(
                Jwts.parser().verifyWith(refreshJwtSecret).build().parseSignedClaims(token).getPayload());
    }

    public String extractUsernameRefreshToken(String token) {
        try {
            return extractClaimsForRefreshToken(token, Claims::getSubject);

        } catch (Exception e) {
            throw new CustomJWTException("Invalid Token.");
        }

    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (userDetails == null || username.equals(userDetails.getUsername())) && !isRefreshTokenExpired(token);
    }

    public boolean isRefreshTokenExpired(String token) {
        try {
            return extractClaims(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            throw new CustomJWTException("Token Expired");
        }
    }
}
