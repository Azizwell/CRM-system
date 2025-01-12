package org.example.crm_system.repository;

import org.example.crm_system.entity.Currencies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepo extends JpaRepository<Currencies, Long> {
  Currencies findByCode(String code);

}
