package org.example.crm_system.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.crm_system.repository.UserRepo;
import org.example.crm_system.service.jwt.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Filter extends OncePerRequestFilter {
  final UserRepo userRepo;
  final JwtService jwtService;


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = request.getHeader("Authorization");

    try {
      if (token != null) {
        String id = jwtService.extractJwtToken(token);
        UserDetails users = userRepo.findById(UUID.fromString(id)).orElseThrow();

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(users.getUsername(), null, users.getAuthorities()));
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    filterChain.doFilter(request, response);
  }
}
