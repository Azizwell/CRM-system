package org.example.crm_system.service.jwt;

import org.example.crm_system.entity.User;

public interface JwtService {
  String generateJwtToken(User user);

  String extractJwtToken(String token);
}
