package com.example.newsfeed.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    private String secretKey = "ThisIsAVerySecureSecretKeyForJwtExample12345ThisIsAVerySecureSecretKeyForJwtExample12345ThisIsAVerySecureSecretKeyForJwtExample12345ThisIsAVerySecureSecretKeyForJwtExample12345";

    // JWT 유효 기간 설정 30분
    private final long expirationMs = 1800000;

    // Bean 초기화 시 자동으로 Base64로 secretKey 인코딩
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 이메일을 기반으로 JWT 토큰 생성
    public String createToken(Long userId, String email) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("userId", userId); // 다른 엔티티에서 FK를 userId로 참조 중이므로 동일하게 사용
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 이메일 추출
    public String getEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 유저 ID (userId) 추출
    public Long getUserId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}

