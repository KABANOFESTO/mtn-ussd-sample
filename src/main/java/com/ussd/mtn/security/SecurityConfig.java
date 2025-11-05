package com.ussd.mtn.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(
                                                request -> new org.springframework.web.cors.CorsConfiguration()
                                                                .applyPermitDefaultValues())) // Enable
                                                                                              // CORS
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/ussd/process",
                                                                "/ussd/admin/menu",
                                                                "/ussd/**",
                                                                "/code**/",
                                                                "/public/**")
                                                .permitAll()
                                                .anyRequest().authenticated());

                return http.build();
        }
}
