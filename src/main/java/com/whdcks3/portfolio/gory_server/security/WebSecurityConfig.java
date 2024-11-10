package com.whdcks3.portfolio.gory_server.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.whdcks3.portfolio.gory_server.security.jwt.AuthEntryPointJwt;
import com.whdcks3.portfolio.gory_server.security.jwt.AuthTokenFilter;
import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;
import com.whdcks3.portfolio.gory_server.security.service.CustomerUserDetailsServiceImpl;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    // @Autowired
    // CustomUserDetails customUserDetails;

    @Autowired
    CustomerUserDetailsServiceImpl customerUserDetailsServiceImpl;

    // @Autowired
    // private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        System.out.println("LOGGING authencationJwtTokenFilter");
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(customerUserDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("LOGGING filterChain");
        http.cors().and().csrf().disable()
                // .httpBasic().disable()
                // .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                // .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().anyRequest().permitAll();
        // .authorizeRequests()
        // .antMatchers("/api/auth/**").permitAll().anyRequest().authenticated();

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // .sessionManagement(
        // s ->
        // s.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId)
        // .sessionCreationPolicy(SessionCreationPolicy.ALWAYS).maximumSessions(50000000)
        // .maxSessionsPreventsLogin(false).expiredUrl("/"));
        return http.build();
    }

    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    // CorsConfiguration configuration = new CorsConfiguration();
    // configuration.setAllowedOrigins(Arrays.asList("https://gory.limchanghwi.com"));
    // configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT",
    // "DELETE"));
    // configuration.setAllowedHeaders(Arrays.asList("*"));
    // configuration.setAllowCredentials(true);
    // configuration.setMaxAge(3600L);
    // ;

    // UrlBasedCorsConfigurationSource source = new
    // UrlBasedCorsConfigurationSource();
    // source.registerCorsConfiguration("/**", configuration);
    // return source;
    // }
}
