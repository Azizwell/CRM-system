package org.example.crm_system.service.admin;

import jakarta.validation.Valid;
import org.example.crm_system.dto.admin.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public interface AdminService {
  ResponseEntity<?> addClient(@Valid ClientDto clientDto);

  HttpEntity<?> getClients();

  HttpEntity<?> updateClient(@Valid UpdateClientDto updateClientDto);

  HttpEntity<?> deleteClient(String clientId);

  HttpEntity<?> addUser(@Valid AddUserDto addUserDto);

  HttpEntity<?> getRoles();

  HttpEntity<?> getUsers();

  HttpEntity<?> getCurrencies();

  HttpEntity<?> getCategory();

  HttpEntity<?> addExpenses(@Valid ExpenseDto expenseDto);

  HttpEntity<?> addIncome(@Valid IncomeDto incomeDto);
}
