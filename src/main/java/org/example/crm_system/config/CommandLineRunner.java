package org.example.crm_system.config;

import lombok.RequiredArgsConstructor;
import org.example.crm_system.entity.*;
import org.example.crm_system.repository.*;
import org.example.crm_system.service.currency_service.CurrencyService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {

  final UserRepo userRepo;
  final PasswordEncoder passwordEncoder;
  final RoleRepo roleRepo;
  final CategoryRepo categoryRepo;
  final ServiceTypeRepo serviceTypeRepo;
  final CurrencyRepo currencyRepo;
  final CurrencyService currencyService;


  @Override
  public void run(String... args) throws Exception {
    List<Role> all = roleRepo.findAll();
    if (all.isEmpty()) {
      roleRepo.saveAll(List.of(
              new Role("ROLE_ADMIN"),
              new Role("ROLE_USER")
      ));
      List<Role> adminRoles = roleRepo.findAdminRoles();
      User user = User.builder().username("admin").fullName("admin")
              .password(passwordEncoder.encode("admin123")).isEnabled(true)
              .roles(adminRoles).build();
      userRepo.save(user);
      categoryRepo.saveAll(List.of(
              new Categories("Salary"),
              new Categories("Dividends"),
              new Categories("Advertising"),
              new Categories("Client Ads"),
              new Categories("Development Fund"),
              new Categories("Fiscal Expenses"),
              new Categories("Household Expenses"),
              new Categories("Services"),
              new Categories("Union"),
              new Categories("Commission Fee")
      ));

      serviceTypeRepo.saveAll(List.of(
              new ServiceType("Website development"),
              new ServiceType("Bot development"),
              new ServiceType("SMM"),
              new ServiceType("Contextual advertising launch"),
              new ServiceType("Target advertising launch"),
              new ServiceType("Branding"),
              new ServiceType("SEO")
      ));

      Map<String, String> currency = currencyService.getCurrency();
      System.out.println(currency);
      BigDecimal rub = BigDecimal.valueOf(Double.parseDouble(currency.get("RUB")));
      BigDecimal usd = BigDecimal.valueOf(Double.parseDouble(currency.get("USD")));
      BigDecimal uzs = BigDecimal.valueOf(1);

      currencyRepo.saveAll(List.of(
              new Currencies("RUB", rub),
              new Currencies("USD", usd),
              new Currencies("UZS", uzs)
      ));


    }


  }
}
