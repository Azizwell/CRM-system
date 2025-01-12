package org.example.crm_system.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.crm_system.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {
  @Value("${secretKey}")
  private String secretKey;

  @Override
  public String generateJwtToken(User user) {
    Map<String, String> claims = Map.of("id", user.getId().toString(), "username", user.getUsername());


    String jwtToken = Jwts.builder().issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
            .claims(claims).subject(user.getId().toString())
            .signWith(signWithKey()).compact();
    return jwtToken;
  }

  @Override
  public String extractJwtToken(String token) {
    Jws<Claims> claimsJws = Jwts.parser().verifyWith(signWithKey()).build().parseSignedClaims(token);
    String id = claimsJws.getPayload().getSubject();
    return id;
  }

  private SecretKey signWithKey() {
    byte[] decode = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(decode);
  }
}
