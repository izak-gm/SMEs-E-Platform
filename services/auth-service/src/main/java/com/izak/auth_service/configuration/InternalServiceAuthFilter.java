package com.izak.auth_service.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InternalServiceAuthFilter extends OncePerRequestFilter {


  private static final Map<String, String> VALID_TOKENS = Map.of(
        "django-order-service", "DJANGO_ORDER_SERVICE_TOKEN",
        "notification-service", "NOTIFICATION_SERVICE_TOKEN",
        "auth-service", "AUTH_SERVICE_TOKEN"
  );

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
        throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    log.info("Auth header: {}", authHeader);
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      if (VALID_TOKENS.containsKey(token)) {
        // Mark request as authenticated
        String serviceName = VALID_TOKENS.get(token);
        List<GrantedAuthority> authorities =
              List.of(new SimpleGrantedAuthority("ROLE_SERVICE"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
              serviceName,
              null,
              authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Internal Service authenticated: {}", serviceName);
        filterChain.doFilter(request, response);
      }
      
    }
  }
}
