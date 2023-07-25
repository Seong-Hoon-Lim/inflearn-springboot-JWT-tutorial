package com.example.jwttutorial.controller;

import com.example.jwttutorial.dto.LoginDTO;
import com.example.jwttutorial.dto.TokenDTO;
import com.example.jwttutorial.jwt.JwtFilter;
import com.example.jwttutorial.jwt.TokenProvider;
import com.example.jwttutorial.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // JWT 토큰 생성 및 검증을 담당하는 TokenProvider 의존성 주입 필드
    private final TokenProvider tokenProvider;

    // 인증 처리를 담당하는 AuthenticationManagerBuilder 의존성 주입 필드
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider,
                          AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDTO> authorize(
            @Valid @RequestBody LoginDTO loginDTO) {
        // 주어진 LoginDTO 객체를 기반으로 인증을 시도하는 UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getMembername(),
                                                        loginDTO.getPassword());
        logger.debug("AuthController: 생성된 authenticationToken {}", authenticationToken);

        // AuthenticationManagerBuilder를 사용하여 authenticationToken을 기반으로 실제 인증 처리를 수행
        // (인증 처리는 AuthenticationManagerBuilder에서 설정한 ProviderManager를 통해 진행됨)
        Authentication authentication = authenticationManagerBuilder.getObject()
                                                                    .authenticate(authenticationToken);

        // 인증 정보를 SecurityContextHolder에 저장하여 현재 스레드의 SecurityContext에 인증 정보를 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 인증에 성공하면 JWT 토큰을 생성
        String jwt = tokenProvider.createToken(authentication);
        logger.debug("AuthController: 생성된 JWT 토큰: {}", jwt);

        // HTTP Response Header에 JWT 토큰을 추가하여 클라이언트에게 반환
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        // JWT 토큰과 HTTP 상태코드 200 OK를 함께 담은 ResponseEntity를 반환
        return new ResponseEntity<>(new TokenDTO(jwt), httpHeaders, HttpStatus.OK);
    }
}