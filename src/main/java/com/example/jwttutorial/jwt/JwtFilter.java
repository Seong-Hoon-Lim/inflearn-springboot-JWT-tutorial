package com.example.jwttutorial.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // JWT 필터의 역할은 클라이언트 요청에서 JWT 토큰을 추출하고, 해당 토큰을 검증하여 인증 정보를 설정
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        // JWT 토큰이 존재하고, 유효성을 검증하는 경우 처리
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

            logger.debug("요청에서 추출한 JWT 토큰: {}", jwt);

            // 추출한 JWT 토큰을 사용하여 인증 정보 가져옴.
            Authentication authentication = tokenProvider.getAuthentication(jwt);

            // SecurityContextHolder를 사용하여 가져온 인증 정보를 SecurityContext에 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.debug("Security Context 에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }
        // 다음 필터로 요청을 전달 만약 다른 필터가 없다면 실제 요청을 처리하는 컨트롤러로 전달됨
        chain.doFilter(request, response);
    }

    // 클라이언트 요청에서 JWT 토큰을 추출하는 메소드
    private String resolveToken(HttpServletRequest request) {

        // Authorization 헤더에서 JWT 토큰을 가져옴
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // JWT 토큰이 존재하고, Bearer 접두사로 시작하는 경우를 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {

            // Bearer 접두사를 제거하고 실제 JWT 토큰만을 반환
            return bearerToken.substring(7);
        }
        return null;
    }
}

