package com.beninexplo.backend.config;

import com.beninexplo.backend.security.jwt.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
        private final java.util.List<String> allowedOriginPatterns;

        public SecurityConfig(
                        JwtAuthFilter jwtAuthFilter,
                        @Value("${app.cors.allowed-origin-patterns}") java.util.List<String> allowedOriginPatterns
        ) {
        this.jwtAuthFilter = jwtAuthFilter;
                this.allowedOriginPatterns = allowedOriginPatterns;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(ct -> {})
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tombola/inscription").permitAll()

                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/uploads/documents/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/activites/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories-activites/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/circuit-activites/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/circuits/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hebergements/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/media/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/parametres-site/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tarifs-circuit-personnalise/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/villes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/zones/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/circuits-personnalises").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/devis").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/devis-activites").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reservations").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reservations-hebergement").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/paiements/hebergement/paypal/config").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/paiements/hebergement/paypal/create-order").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/paiements/hebergement/paypal/capture-order").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations-hebergement/disponibilite").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reservations-hebergement/indisponibilites/**").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/tombola/**").hasRole("PARTICIPANT")

                        .requestMatchers(HttpMethod.GET, "/api/circuits-personnalises").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/circuits-personnalises/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/circuits-personnalises/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/circuits-personnalises/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/devis/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/devis/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/devis-activites/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/devis-activites/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/reservations/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/reservations-hebergement/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations-hebergement/me/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations-hebergement").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations-hebergement/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations-hebergement/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations-hebergement/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/activites/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/activites/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/activites/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/categories-activites/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories-activites/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories-activites/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/circuit-activites/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/circuit-activites/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/circuit-activites/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/circuits/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/circuits/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/circuits/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/hebergements/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/hebergements/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/hebergements/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/images/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/media/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/media/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/media/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/parametres-site/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/parametres-site/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/parametres-site/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tarifs-circuit-personnalise/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tarifs-circuit-personnalise/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tarifs-circuit-personnalise/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/villes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/villes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/villes/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/zones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/zones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/zones/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"
        ));
        configuration.setExposedHeaders(Arrays.asList("Content-Disposition", "Location"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
