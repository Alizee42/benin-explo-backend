package com.beninexplo.backend.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Clé secrète (générée, longue, correcte)
    private static final String SECRET_KEY = 
            "6D597133743677397A24432646294A404E635266556A586E3272357538782F41";

    // Durée de validité du token : 24h
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000;

    /* ----------------------------------------------------
       🟦 EXTRACTION DE LA CLÉ SIGNATURE
    ---------------------------------------------------- */
    private Key getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

    /* ----------------------------------------------------
       🟩 GÉNERER UN TOKEN (email + role)
    ---------------------------------------------------- */
    public String generateToken(String email, String role) {

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)                  // Rôle ajouté au token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ----------------------------------------------------
       🟧 EXTRAIRE L'EMAIL DEPUIS LE TOKEN
    ---------------------------------------------------- */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /* ----------------------------------------------------
       🟪 EXTRAIRE LE RÔLE DEPUIS LE TOKEN
    ---------------------------------------------------- */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /* ----------------------------------------------------
       🟥 VALIDER UN TOKEN
    ---------------------------------------------------- */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /* ----------------------------------------------------
       🟦 EXTRACTION DES CLAIMS
    ---------------------------------------------------- */
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}