package com.izak.auth_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
  @Value("${secret_key}")

  private String SECRET_KEY;
  public String extractUsername(String token){
    return extractClaim(token,Claims::getSubject);
  }
  public String generateToken(UserDetails userDetails) {
    return generateTokenUserDetails(new HashMap<>(),userDetails);
  }

  private String generateTokenUserDetails(Map<String, Object> ExtraClaims, UserDetails userDetails) {
    extraClaims.put
  }
}
