package com.example.jwttutorial.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends
            SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private TokenProvider tokenProvider;

    public JwtSecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // JwtFilter를 Spring Security 필터 체인에 추가하는 역할을 담당하는 메소드
    @Override
    public void configure(HttpSecurity http) {

        // 주입받은 TokenProvider 객체를 사용하여 JwtFilter 인스턴스를 생성
        JwtFilter customFilter = new JwtFilter(tokenProvider);

        // JwtFilter를 UsernamePasswordAuthenticationFilter 이전에 추가
        // JwtFilter는 인증 정보를 검증하고 인증된 사용자에 대한 SecurityContext를 설정
        // UsernamePasswordAuthenticationFilter는 사용자의 아이디와 비밀번호로 인증을 처리하는 필터
        http.addFilterBefore(customFilter,
                            UsernamePasswordAuthenticationFilter.class);
    }
}
