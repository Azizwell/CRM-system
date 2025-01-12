package org.example.crm_system.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.crm_system.dto.user.LoginUserDto;
import org.example.crm_system.dto.user.UserDto;
import org.example.crm_system.entity.Role;
import org.example.crm_system.entity.User;
import org.example.crm_system.repository.RoleRepo;
import org.example.crm_system.repository.UserRepo;
import org.example.crm_system.service.jwt.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  final RoleRepo roleRepo;
  final UserRepo userRepo;
  final PasswordEncoder passwordEncoder;
  final AuthenticationManager authenticationManager;
  final JwtService jwtService;


  @Override
  public ResponseEntity<?> registerUser(UserDto userDto) {
    List<Role> roleUser = roleRepo.findAllByName("ROLE_USER");

    User user = User.builder().username(userDto.username()).fullName(userDto.fullName())
            .password(passwordEncoder.encode(userDto.password()))
            .roles(roleUser)
            .isEnabled(true).build();
    userRepo.save(user);

    return ResponseEntity.ok("registered");
  }

  @Override
  public ResponseEntity<?> loginUser(LoginUserDto loginUserDto) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginUserDto.username(), loginUserDto.password())
    );

    User users = userRepo.findByUsername(loginUserDto.username()).orElseThrow();
    String jwtToken = jwtService.generateJwtToken(users);

    String rollName = users.getRoles() != null && !users.getRoles().isEmpty()
            ? users.getRoles().get(0).getName()
            : "UNKNOWN_ROLE";

    HashMap<String, String> map = new HashMap<>();
    map.put("accessToken", jwtToken);
    map.put("login", users.getUsername());
    map.put("fullName", users.getFullName());
    map.put("role", rollName);


    return ResponseEntity.ok(map);
  }


}
