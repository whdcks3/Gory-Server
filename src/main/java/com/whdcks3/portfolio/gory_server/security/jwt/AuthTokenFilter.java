package com.whdcks3.portfolio.gory_server.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;
import com.whdcks3.portfolio.gory_server.security.service.CustomerUserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    CustomerUserDetailsServiceImpl userDetailsServ;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getUserNameFromJwtToken(jwt);
                UserDetails userdetails = userDetailsServ.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userdetails, null, userdetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    public String parseJwt(HttpServletRequest req) {
        String headerAuthorization = req.getHeader("Authorization");
        if (StringUtils.hasText(headerAuthorization) && headerAuthorization.startsWith("Bearer ")) {
            return headerAuthorization.substring(7);
        }
        return null;
    }
}
