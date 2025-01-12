package org.example.crm_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Transactions {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  private Type type;

  private BigDecimal amount;
  private String currency;
  private BigDecimal amountInUZS;
  @CreationTimestamp
  private LocalDate transactionDate;

  @ManyToOne
  private Categories category;

  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToOne
  private Clients client;

  private String description;


}
