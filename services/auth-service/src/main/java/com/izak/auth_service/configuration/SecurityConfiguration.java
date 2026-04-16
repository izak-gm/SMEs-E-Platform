package com.izak.auth_service.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  private static final String[] WHITE_LIST_URL = {
        "/v1/api/auth/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
  };

  private static final String[] WHITE_LIST_USER_URL = {
        "/v1/api/auth/me/**"
  };

  private static final String[] WHITE_LIST_SELLER_URL = {
        "/v1/api/auth/me/**",
        "/v1/api/auth/buyers/**"
  };

  private static final String[] WHITE_LIST_ADMIN_URL = {
        "/v1/api/auth/me/**",
        "/v1/api/auth/users/**",
        "/v1/api/auth/buyers/**",
        "/v1/api/auth/sellers/**"
  };

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final InternalServiceAuthFilter internalServiceAuthFilter;
  private final CorsConfigurationSource corsConfigurationSource; //  Injected bean

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
          .cors(cors -> cors.configurationSource(corsConfigurationSource)) //  use external config
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITE_LIST_URL).permitAll()
                .requestMatchers(WHITE_LIST_SELLER_URL).hasRole("SELLER")
                .requestMatchers(WHITE_LIST_USER_URL).hasRole("USER")
                .requestMatchers(WHITE_LIST_ADMIN_URL).hasAnyRole("ADMIN", "SERVICE")
                .anyRequest().fullyAuthenticated()
          )
          .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .addFilterBefore(internalServiceAuthFilter, UsernamePasswordAuthenticationFilter.class)
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
          .build();
  }
}
