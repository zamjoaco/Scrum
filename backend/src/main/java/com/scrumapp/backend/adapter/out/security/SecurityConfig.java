package com.scrumapp.backend.adapter.out.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scrumapp.backend.adapter.in.web.ErrorTypes;
import java.io.IOException;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final int BCRYPT_STRENGTH = 12;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/auth/refresh",
                                "/auth/password/reset-request",
                                "/auth/password/reset-confirm")
                        .permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(problemDetailAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(problemDetailAccessDeniedHandler(objectMapper)));

        // Aca debe enganchar el filtro de JWT real (lo agrega el worker de Auth),
        // por ejemplo: .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        return http.build();
    }

    private AuthenticationEntryPoint problemDetailAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, authException) -> {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED, "No se proveyo un token valido, o el token expiro.");
            problemDetail.setType(URI.create(ErrorTypes.UNAUTHORIZED));
            problemDetail.setTitle("No autenticado");
            problemDetail.setInstance(URI.create(request.getRequestURI()));
            writeProblemDetail(response, objectMapper, problemDetail, HttpStatus.UNAUTHORIZED);
        };
    }

    private AccessDeniedHandler problemDetailAccessDeniedHandler(ObjectMapper objectMapper) {
        return (request, response, accessDeniedException) -> {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.FORBIDDEN, "El usuario autenticado no tiene permisos para esta operacion.");
            problemDetail.setType(URI.create(ErrorTypes.FORBIDDEN));
            problemDetail.setTitle("Acceso denegado");
            problemDetail.setInstance(URI.create(request.getRequestURI()));
            writeProblemDetail(response, objectMapper, problemDetail, HttpStatus.FORBIDDEN);
        };
    }

    private void writeProblemDetail(
            jakarta.servlet.http.HttpServletResponse response,
            ObjectMapper objectMapper,
            ProblemDetail problemDetail,
            HttpStatus status)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }
}
