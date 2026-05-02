package com.racecontrol.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.expiration-ms}")
  private long exp;
  @Value("${jwt.refresh-expiration-ms}")
  private long refreshExp;

  private SecretKey key() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String accessToken(String subject) {
    return Jwts.builder().subject(subject).expiration(new Date(System.currentTimeMillis() + exp)).signWith(key()).compact();
  }

  public String refreshToken(String subject) {
    return Jwts.builder().subject(subject).expiration(new Date(System.currentTimeMillis() + refreshExp)).signWith(key()).compact();
  }

  public String subject(String token) {
    return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
  }
}
