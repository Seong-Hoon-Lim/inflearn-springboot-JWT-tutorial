package com.example.jwttutorial.config;

import com.example.jwttutorial.jwt.JwtAccessDeniedHandler;
import com.example.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import com.example.jwttutorial.jwt.JwtSecurityConfig;
import com.example.jwttutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity // Spring Security를 활성화하는 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true) // 메소드 수준에서의 보안 처리를 활성화하는 어노테이션
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider; // JWT 토큰 생성 및 검증을 담당하는 TokenProvider 의존성 주입 필드
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 인증 실패 시 처리하는 핸들러 주입 필드
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler; // 인가 거부 시 처리하는 핸들러 주입 필드

    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCryptPasswordEncoder를 빈으로 등록하여 비밀번호 인코딩에 사용
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                // "/h2-console/**"와 "/favicon.ico" 경로의 요청은 시큐리티 무시하여 인증/인가 처리 하지 않음
                .antMatchers(
                        "/h2-console/**",
                        "/favicon.ico"
                );
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()

                .exceptionHandling()
                // JWT 인증 실패 시 사용되는 핸들러 설정
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // JWT 접근 거부 시 사용되는 핸들러 설정
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .headers()
                // X-Frame-Options를 SAMEORIGIN으로 설정하여 동일 출처에서만 iframe으로 렌더링 허용
                .frameOptions()
                .sameOrigin()

                .and()
                .sessionManagement()
                // 세션 생성 정책을 STATELESS로 설정하여 세션을 사용하지 않음
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                // HttpServletRequest를 사용하는 요청에 대한 접근 제한 설정
                .authorizeRequests()
                // "/api/hello" 경로에 대한 요청은 인증 없이 접근 허용
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/signup").permitAll()
                // 나머지 요청에 대해서는 모두 인증을 받아야 함
                .anyRequest().authenticated()

                .and()
                // JwtSecurityConfig를 적용하여 JWT 인증 설정
                .apply(new JwtSecurityConfig(tokenProvider));

    }
}
