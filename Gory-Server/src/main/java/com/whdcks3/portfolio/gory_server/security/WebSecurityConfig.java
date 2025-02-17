package com.whdcks3.portfolio.gory_server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.whdcks3.portfolio.gory_server.security.jwt.AuthEntryPointJwt;
import com.whdcks3.portfolio.gory_server.security.jwt.AuthTokenFilter;
import com.whdcks3.portfolio.gory_server.security.service.CustomerUserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    // @Autowired
    // CustomUserDetails customUserDetails;

    @Autowired
    CustomerUserDetailsServiceImpl customerUserDetailsServiceImpl;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    // @Bean
    // public AuthTokenFilter authenticationJwtTokenFilter() {
    // System.out.println("LOGGING authencationJwtTokenFilter");
    // return new AuthTokenFilter();
    // }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(customerUserDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // 비밀번호를 암호화하고 검증할 때 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        System.out.println("AuthenticationManager 빈 생성");
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("LOGGING filterChain started");
        http.cors().and().csrf().disable()
                // .httpBasic().disable()
                // 인증되지 않은 요청이 발생했을 때 AuthEntryPointJwt가 처리
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                // 모든 요청에 대해 JWT 토큰 인증
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // .authorizeRequests().anyRequest().permitAll();
                .authorizeRequests() // 요청별 인증 규칙
                // /api/auth/**는 인증 없이 접근 가능, 나머지 요청은 인증 필요
                .antMatchers("/api/auth/**", "/api/user/**", "/api/feed/**", "/api/squad/**", "/swagger-ui/**",
                        "/webjars/**", "/swagger-ui.html", "/v3/api-docs/**")
                .permitAll().anyRequest()
                .authenticated();

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // .sessionManagement(
        // s ->
        // s.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId)
        // .sessionCreationPolicy(SessionCreationPolicy.ALWAYS).maximumSessions(50000000)
        // .maxSessionsPreventsLogin(false).expiredUrl("/"));
        System.out.println("LOGGING filterChain ended");
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
