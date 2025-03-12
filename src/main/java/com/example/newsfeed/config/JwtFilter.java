package com.example.newsfeed.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JwtFilter")
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request); // Authorization 헤더에서 토큰 추출

        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.getEmail(token);  // JWT에서 이메일 추출
            Long userId = jwtUtil.getUserId(token);  // JWT에서 사용자 ID 추출

            // 인증된 사용자 정보를 request에 설정
            request.setAttribute("email", email);
            request.setAttribute("userId", userId);  // Long 타입으로 설정

            log.info("JWT 인증 성공 - 사용자 이메일: {}", email);
        } else {
            log.info("JWT가 없거나 유효하지 않습니다.");
        }

        filterChain.doFilter(request, response);  // 요청을 다음 필터로 전달
    }

    // JWT 토큰을 헤더에서 추출하는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 제외한 토큰 반환
        }

        return null;  // JWT가 없으면 null 반환
    }
}




