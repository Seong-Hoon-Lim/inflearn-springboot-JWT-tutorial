package com.example.jwttutorial.jwt;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // 클라이언트에게 403 Forbidden 상태 코드를 반환하여 접근 거부 상태를 알려줌.
    // 접근이 거부되었을 때 발생하는 예외인 AccessDeniedException을 처리하는 메소드
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // HttpServletResponse의 sendError 메소드를 사용하여 클라이언트에게 403 Forbidden 상태 코드를 반환.
        // 이를 통해 클라이언트는 접근이 거부되었음을 인지하고 해당 상태 코드에 따른 적절한 처리를 수행
        response.sendError(HttpServletResponse.SC_FORBIDDEN);

    }
}
