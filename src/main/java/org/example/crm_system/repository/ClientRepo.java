package org.example.crm_system.repository;

import org.example.crm_system.entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepo extends JpaRepository<Clients, UUID> {


}
