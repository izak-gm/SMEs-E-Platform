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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private static final String[] WHITE_LIST_URL={"api/v1/auth/**"};
  private static final String[] WHITE_LIST_USER_URL={"api/v1/auth/user/**"};
  private static final String[] WHITE_LIST_ADMIN_URL={"api/v1/auth/admin/**"};

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception{
    return httpSecurity
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests( auth->auth
                .requestMatchers(WHITE_LIST_URL).permitAll()
                .requestMatchers(WHITE_LIST_ADMIN_URL).hasRole("ADMIN")
                .requestMatchers(WHITE_LIST_USER_URL).hasRole("USER")
                .anyRequest().fullyAuthenticated()
          )
          .sessionManagement(session->session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
          .build();
  }
}
