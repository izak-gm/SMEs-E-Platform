package com.izak.auth_service.configuration;

import com.izak.auth_service.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${secret_key}")
  private String SECRET_KEY;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
          .verifyWith(getSignKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
  }

  private SecretKey getSignKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  }

  // ✅ Generate token with User entity (includes id, email, and role)
  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("id", user.getId());
    claims.put("email", user.getEmail());
    claims.put("roles", user.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .toList());

    return buildToken(claims, user.getEmail());
  }

  private String buildToken(Map<String, Object> claims, String subject) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
          .claims(claims)
          .subject(subject)
          .issuedAt(new Date(now))
          // Set expiration (1 hour)
          .expiration(new Date(now + 1000 * 60 * 60))
          .signWith(getSignKey())
          .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
