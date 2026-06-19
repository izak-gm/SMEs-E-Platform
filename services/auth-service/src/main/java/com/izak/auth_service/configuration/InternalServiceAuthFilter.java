package com.izak.auth_service.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class InternalServiceAuthFilter extends OncePerRequestFilter {

  private final InternalServiceTokens tokens;

  @Override
  protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
  ) throws ServletException, IOException {

    String token = request.getHeader("X-API-KEY");

    Map<String, String> serviceTokens = Map.of(
          "django-order-service", tokens.getDjangoToken(),
          "notification-service", tokens.getNotificationToken(),
          "auth-service", tokens.getAuthToken()
    );

    String serviceName = serviceTokens.entrySet()
          .stream()
          .filter(entry -> entry.getValue().equals(token))
          .map(Map.Entry::getKey)
          .findFirst()
          .orElse(null);

    if (token != null && serviceName == null) {
      response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            "Invalid internal API key"
      );
      return;
    }

    if (serviceName != null) {

      Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                  serviceName,
                  null,
                  List.of(new SimpleGrantedAuthority("ROLE_SERVICE"))
            );

      SecurityContextHolder.getContext()
            .setAuthentication(authentication);

      request.setAttribute("isService", true);
      request.setAttribute("serviceName", serviceName);

      log.info("Authenticated internal service: {}", serviceName);
    }

    filterChain.doFilter(request, response);
  }
}