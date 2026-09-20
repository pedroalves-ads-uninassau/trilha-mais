package com.trilhamais.backend.config;

import com.trilhamais.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Servico responsavel pela geracao, extracao e validacao criptografica de tokens JWT.
 * Utiliza o algoritmo HMAC-SHA256 via biblioteca JJWT.
 */
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Gera um token de acesso assinado contendo informacoes seguras do estudante.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrai o e-mail (subject) de dentro do token JWT.
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrai o ID do usuario de dentro do token JWT.
     */
    public Long extractUserId(String token) {
        Object idVal = extractAllClaims(token).get("userId");
        if (idVal instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    /**
     * Valida se o token e estruturalmente autentico e nao expirou.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Valida se o token pertence ao usuario informado e nao expirou.
     */
    public boolean isTokenValid(String token, User user) {
        try {
            Claims claims = extractAllClaims(token);
            boolean isEmailMatching = claims.getSubject().equalsIgnoreCase(user.getEmail());
            boolean isNotExpired = claims.getExpiration().after(new Date());
            return isEmailMatching && isNotExpired;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
