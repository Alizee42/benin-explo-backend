package com.beninexplo.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.beninexplo.backend.security.jwt.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /* ----------------------------------------------------
       🟦 FILTER CHAIN (règles de sécurité)
    ---------------------------------------------------- */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Auth public
                        .requestMatchers("/auth/**").permitAll()

                        // Endpoints Admin uniquement
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Endpoints User uniquement
                        .requestMatchers("/user/**").hasRole("USER")

                        // Endpoints Participant (tombola)
                        .requestMatchers("/tombola/**").hasRole("PARTICIPANT")

                        // Le reste est autorisé ou protégé ?
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* ----------------------------------------------------
       🟩 AUTHENTICATION MANAGER (Spring Security 3)
    ---------------------------------------------------- */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
