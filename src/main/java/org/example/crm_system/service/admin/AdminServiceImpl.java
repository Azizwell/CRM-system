package org.example.crm_system.service.admin;

import lombok.AllArgsConstructor;
import org.example.crm_system.dto.admin.*;
import org.example.crm_system.entity.*;
import org.example.crm_system.repository.*;
import org.example.crm_system.service.currency_service.CurrencyService;
import org.example.crm_system.service.notion_service.NotionService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
  private ClientRepo clientRepo;
  private ServiceTypeRepo serviceTypeRepo;
  private UserRepo userRepo;
  private PasswordEncoder passwordEncoder;
  private RoleRepo roleRepo;
  private CurrencyService currencyService;
  private CurrencyRepo currencyRepo;
  private CategoryRepo categoryRepo;
  private TransactionRepo transactionRepo;
  private NotionService notionService;

  @Override
  public ResponseEntity<?> addClient(ClientDto clientDto) {
    ServiceType serviceType = serviceTypeRepo.findById(clientDto.serviceTypeId()).orElseThrow();
    clientRepo.save(Clients.builder().name(clientDto.name()).phone(clientDto.phone()).serviceType(serviceType).build());
    return ResponseEntity.ok("client added successfully");
  }

  @Override
  public HttpEntity<?> getClients() {
    return ResponseEntity.ok(clientRepo.findAll());
  }

  @Override
  public HttpEntity<?> updateClient(UpdateClientDto updateClientDto) {
    Clients client = clientRepo.findById(UUID.fromString(updateClientDto.clientId())).orElseThrow();
    ServiceType serviceType = serviceTypeRepo.findById(updateClientDto.serviceTypeId()).orElseThrow();
    client.setName(updateClientDto.name());
    client.setPhone(updateClientDto.phone());
    client.setServiceType(serviceType);
    clientRepo.save(client);
    return ResponseEntity.ok("client updated successfully");
  }

  @Override
  public HttpEntity<?> deleteClient(String clientId) {
    clientRepo.deleteById(UUID.fromString(clientId));
    return ResponseEntity.ok("client deleted successfully");
  }

  @Override
  public HttpEntity<?> addUser(AddUserDto addUserDto) {
    List<Role> roles = roleRepo.queryFindById(UUID.fromString(addUserDto.rolId()));
    userRepo.save(User.builder().username(addUserDto.username()).fullName(addUserDto.fullName())
            .password(passwordEncoder.encode(addUserDto.password())).isEnabled(true).roles(roles).build());
    return ResponseEntity.ok("User added successfully");
  }

  @Override
  public HttpEntity<?> getRoles() {
    return ResponseEntity.ok(roleRepo.findAll());
  }

  @Override
  public HttpEntity<?> getUsers() {
    return ResponseEntity.ok(userRepo.queryGetAllUsers());
  }

  @Override
  public HttpEntity<?> getCurrencies() {
    Map<String, String> currency = currencyService.getCurrency();
    BigDecimal rub = BigDecimal.valueOf(Double.parseDouble(currency.get("RUB")));
    BigDecimal usd = BigDecimal.valueOf(Double.parseDouble(currency.get("USD")));
    List<Currencies> allCurrencies = currencyRepo.findAll();
    for (Currencies allCurrency : allCurrencies) {
      if (allCurrency.getCode().equals("USD")) {
        allCurrency.setRate(usd);
      }
      if (allCurrency.getCode().equals("RUB")) {
        allCurrency.setRate(rub);
      }

    }
    List<Currencies> currencies = currencyRepo.saveAll(allCurrencies);


    return ResponseEntity.ok(currencies);
  }

  @Override
  public HttpEntity<?> getCategory() {
    return ResponseEntity.ok(categoryRepo.findAll());
  }

  @Override
  @Transactional
  public HttpEntity<?> addExpenses(ExpenseDto expenseDto) {
    Categories categories = categoryRepo.findById(expenseDto.categoryId()).orElseThrow();

    BigDecimal amountInUZS = calculateCurrency(expenseDto.amount(), expenseDto.currency());


    if (amountInUZS == null) {
      throw new IllegalArgumentException("Currency code not found: " + expenseDto.currency());
    }

    LocalDate transactionDate = null;
    if (expenseDto.date() != null) {
      transactionDate = expenseDto.date();
    }

    Transactions build = Transactions.builder().type(Type.valueOf(expenseDto.type()))
            .amount(expenseDto.amount()).amountInUZS(amountInUZS)
            .currency(expenseDto.currency()).category(categories)
            .description(expenseDto.description()).transactionDate(transactionDate).build();
    Transactions save = transactionRepo.save(build);


//    notionService.saveTransactionToNotion(
//            save.getId(),
//            save.getType(),
//            save.getCategory().getId(),
//            "",
//            save.getDescription(),
//            save.getAmount(),
//            save.getAmountInUZS(),
//            save.getCurrency()
//    );
    
    return ResponseEntity.ok("expenses added successfully");
  }

  @Override
  @Transactional
  public HttpEntity<?> addIncome(IncomeDto incomeDto) {
    Clients clients = clientRepo.findById(UUID.fromString(incomeDto.clientId())).orElseThrow();
    BigDecimal amountInUZS = calculateCurrency(incomeDto.amount(), incomeDto.currency());

    if (amountInUZS == null) {
      throw new IllegalArgumentException("Currency code not found: " + incomeDto.currency());
    }

    LocalDate transactionDate = null;
    if (incomeDto.date() != null) {
      transactionDate = incomeDto.date();
    }

    Transactions build = Transactions.builder().type(Type.valueOf(incomeDto.type())).amount(incomeDto.amount())
            .amountInUZS(amountInUZS).currency(incomeDto.currency()).status(Status.valueOf(incomeDto.status()))
            .client(clients).description(incomeDto.description())
            .transactionDate(transactionDate).build();
    transactionRepo.save(build);
    return ResponseEntity.ok("income added successfully");
  }


  BigDecimal calculateCurrency(BigDecimal decimalAmount, String currencyCode) {
    List<Currencies> allCurrencies = currencyRepo.findAll();
    for (Currencies allCurrency : allCurrencies) {
      if (allCurrency.getCode().equals(currencyCode)) {

        return decimalAmount.multiply(allCurrency.getRate());
      }
    }
    return null;
  }

}
