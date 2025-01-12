package org.example.crm_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.crm_system.dto.admin.*;
import org.example.crm_system.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AdminController {
  @Autowired
  private AdminService adminService;


  // круд клиентов
  @PostMapping("/add_client")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> addClient(@RequestBody ClientDto clientDto) {
    return adminService.addClient(clientDto);
  }

  @GetMapping("/clients")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> getClients() {
    return adminService.getClients();
  }

  @PutMapping("/update_client")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> updateClient(@Valid @RequestBody UpdateClientDto updateClientDto) {
    return adminService.updateClient(updateClientDto);
  }

  @DeleteMapping("/delete_client")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> deleteClient(@RequestParam String clientId) {
    return adminService.deleteClient(clientId);
  }

  //круд админ, ползователи
  @GetMapping("/users")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> getUsers() {
    return adminService.getUsers();
  }

  @GetMapping("/roles")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> getRoles() {
    return adminService.getRoles();
  }

  @PostMapping("/add_user")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> addUser(@Valid @RequestBody AddUserDto addUserDto) {
    return adminService.addUser(addUserDto);
  }

  //круд транзакция
  @GetMapping("/currencies")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> getCurrencies() {
    return adminService.getCurrencies();
  }

  //расход
  @GetMapping("/category")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> getCategory() {
    return adminService.getCategory();
  }

  @PostMapping("/expenses")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> addExpenses(@Valid @RequestBody ExpenseDto expenseDto) {
    return adminService.addExpenses(expenseDto);

  }

  //доход
  @PostMapping("/income")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public HttpEntity<?> addIncome(@Valid @RequestBody IncomeDto incomeDto) {
    return adminService.addIncome(incomeDto);
  }


}
