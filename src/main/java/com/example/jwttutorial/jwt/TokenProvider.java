package com.example.jwttutorial.jwt;

import com.example.jwttutorial.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰의 생성, 검증, 사용자 정보 추출 등의 역할을 수행
 * afterPropertiesSet: InitializingBean 인터페이스의 메소드로, 빈이 생성된 후 초기화 작업을 수행
 *
 * createToken: 인증 정보를 바탕으로 JWT 토큰을 생성하는 메소드입니다.
 * 인증 정보를 토큰의 클레임(claim)으로 저장하고, HS512 알고리즘을 사용하여 서명
 *
 * getAuthentication: 주어진 JWT 토큰으로부터 사용자 정보와 권한 정보를 추출하여 Authentication 객체 생성
 * 추출한 정보를 기반으로 사용자를 인증
 *
 * validateToken: 주어진 JWT 토큰의 유효성을 검사하는 메소드
 * 서명의 유효성 및 만료 여부 등을 확인 유효한 토큰인지 검증 결과를 반환
 */
@Component
public class TokenProvider implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {

        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    // Secret 키를 Base64 디코딩하여 Key 객체로 초기화
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 인증 정보를 바탕으로 JWT 토큰을 생성하는 메소드
    public String createToken(Authentication authentication) {
        logger.info("TokenProvider: createToken...");
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date().getTime());
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        // JWT 토큰 생성
        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

        logger.debug("생성된 JWT 토큰: {}", token);

        return token;
    }

    // JWT 토큰에서 사용자 정보와 권한 정보를 추출하여 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 추출한 정보를 기반으로 사용자를 인증하여 Authentication 객체 생성
        Member principal = new Member(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 주어진 JWT 토큰의 유효성을 검사하는 메소드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}

