package org.example.crm_system.repository;

import org.example.crm_system.entity.Categories;
import org.example.crm_system.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTypeRepo extends JpaRepository<ServiceType, Long> {
}
