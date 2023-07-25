package com.example.jwttutorial.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public SecurityUtil() {

    }

    /**
     * 현재 인증된 사용자의 이름을 가져오는 정적 메소드
     * @return 인증된 사용자의 이름을 Optional 객체로 반환 인증된 사용자가 없으면 빈 Optional을 반환
     */
    public static Optional<String> getCurrentUsername() {
        // 현재 인증 정보를 SecurityContextHolder에서 가져옴
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없는 경우 빈 Optional을 반환
        if (authentication == null) {
            logger.debug("Security Context 에 인증 정보가 없습니다");
            return Optional.empty();
        }

        // 인증된 사용자의 이름을 추출
        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            // Spring Security의 UserDetails 타입으로 캐스팅하여 사용자 이름을 가져옴
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            // Principal이 문자열인 경우 사용자 이름을 가져옴
            username = (String) authentication.getPrincipal();
        }

        // 사용자 이름을 Optional 객체로 반환
        return Optional.ofNullable(username);
    }
}

