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

        // HTTP 요청에서 JWT 토큰 추출
        String token = resolveToken(request);

        // JWT 토큰이 존재하고 유효하면 사용자 정보를 요청에 저장
        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.getEmail(token);
            Long userId = jwtUtil.getUserId(token);

            // 사용자 정보를 request에 저장 (ArgumentResolver에서 사용 예정)
            request.setAttribute("email", email);
            request.setAttribute("userId", userId);

            log.info("JWT 인증 성공 - 사용자 이메일: {}", email);
        } else {
            log.info("JWT가 없거나 유효하지 않습니다.");
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 JWT 토큰을 가져오는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // "Bearer " 접두사가 있는지 확인 후 제거하고 토큰만 반환
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 헤더에 JWT가 없으면 null 반환
        return null;
    }
}

