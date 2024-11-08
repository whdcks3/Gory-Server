package com.whdcks3.portfolio.gory_server.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String failedType = (String) request.getAttribute("failedType");
        if (failedType != null) {
            if (failedType.equals("locked")) {
                String lockedTime = (String) request.getAttribute("lockedTime");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("계정이 잠겨있습니다. " + lockedTime + "에 잠금이 해제됩니다.");
            } else if (failedType.equals("inactive")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("인증되지 않은 계정입니다. 메일을 확인하여 인증해주세요.");
            }
        }
        // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // final Map<String, Object> body = new HashMap<>();
        // body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        // body.put("error", "Unauthorized");
        // body.put("message", authException.getMessage());
        // body.put("path", request.getServletPath());

        // final ObjectMapper mapper = new ObjectMapper();
        // mapper.writeValue(response.getOutputStream(), body);

    }
}
