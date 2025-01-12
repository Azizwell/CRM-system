package org.example.crm_system.service.auth;

import org.example.crm_system.dto.user.LoginUserDto;
import org.example.crm_system.dto.user.UserDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
  ResponseEntity<?> registerUser(UserDto userDto);


  ResponseEntity<?> loginUser(LoginUserDto loginUserDto);
}
