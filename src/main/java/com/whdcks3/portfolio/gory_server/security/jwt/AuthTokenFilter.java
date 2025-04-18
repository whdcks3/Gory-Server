package com.whdcks3.portfolio.gory_server.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

// OncePerRequestFilter requst중에서 필터링을 한번만 할수 있게 해줌
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                if (request.getRequestURI().equals("/api/auth/refresh")) {
                    jwtUtils.validateJwtToken(token);
                    filterChain.doFilter(request, response);
                    return;
                }
                Claims claims = jwtUtils.validateJwtToken(token);

                String username = claims.getSubject();

                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                var authencation = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authencation);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"message\": \"유효하지 않은 토큰입니다.\"}");
                return;
            }
        }
        // 필터체인의 다음단계로 넘김
        filterChain.doFilter(request, response);
    }
}
