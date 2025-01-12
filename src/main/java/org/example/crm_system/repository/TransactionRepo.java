package org.example.crm_system.repository;

import org.example.crm_system.entity.Transactions;
import org.example.crm_system.projection.GetTransactionBySelectedSetting;
import org.example.crm_system.projection.MonthlyExpensProjection;
import org.example.crm_system.projection.MonthlyIncomeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepo extends JpaRepository<Transactions, UUID> {

  @Query(value = "SELECT \n" +
          " t.id, t.transaction_date as month,  t.status, t.amount, t.currency, t.amount_inuzs, t.description, t.type, cs.name AS clientName, st.name \n" +
          " FROM transactions t \n" +
          " LEFT JOIN clients cs ON t.client_id = cs.id \n" +
          " LEFT JOIN service_type st ON cs.service_type_id = st.id \n" +
          " WHERE t.type = 'INCOME' AND t.transaction_date >= date_trunc('month', current_date) \n" +
          " AND t.transaction_date < date_trunc('month', current_date) + interval '1 month'", nativeQuery = true)
  List<MonthlyIncomeProjection> getMonthlyIncome();

  @Query(value = "SELECT \n" +
          " t.id, t.transaction_date as month,  t.amount, t.currency, t.amount_inuzs, t.description, t.type, c.name \n" +
          " FROM transactions t \n" +
          " LEFT JOIN categories c ON t.category_id = c.id \n" +
          " WHERE t.type = 'EXPENSE' AND t.transaction_date >= date_trunc('month', current_date) \n" +
          " AND t.transaction_date < date_trunc('month', current_date) + interval '1 month'", nativeQuery = true)
  List<MonthlyExpensProjection> getMonthlyExpense();

  @Query(value = "SELECT t.transaction_date, " +
          "       t.type, " +
          "       t.status, " +
          "       t.amount, " +
          "       t.currency, " +
          "       t.amount_inuzs, " +
          "       t.description, " +
          "       c.name AS category, " +
          "       cs.name AS clientName, " +
          "       st.name AS serviceType " +
          " FROM transactions t " +
          "          LEFT JOIN categories c ON t.category_id = c.id " +
          "          LEFT JOIN clients cs ON t.client_id = cs.id " +
          "          LEFT JOIN service_type st ON cs.service_type_id = st.id " +
          " WHERE (:year IS NULL OR t.transaction_date >= DATE_TRUNC('month', CAST(CONCAT(:year, '-', :month, '-01') AS DATE))) " +
          "   AND (:year IS NULL OR t.transaction_date < DATE_TRUNC('month', CAST(CONCAT(:year, '-', :month, '-01') AS DATE)) + INTERVAL '1 month') " +
          "   AND (:type IS NULL OR t.type = :type) " +
          "   AND (:currency IS NULL OR t.currency = :currency)",
          nativeQuery = true)
  List<GetTransactionBySelectedSetting> getTransactionsBySelectedSetting(
          @Param("year") Integer year,
          @Param("month") Integer month,
          @Param("type") String type,
          @Param("currency") String currency);


}
