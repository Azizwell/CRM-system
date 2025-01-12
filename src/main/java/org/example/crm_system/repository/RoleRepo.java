package org.example.crm_system.repository;

import org.example.crm_system.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RoleRepo extends JpaRepository<Role, UUID> {
  List<Role> findAllByName(String name);

  @Query(value = "select * from role where name IN ('ROLE_ADMIN')", nativeQuery = true)
  List<Role> findAdminRoles();

  @Query(value = "select * from role where id = :roleId ", nativeQuery = true)
  List<Role> queryFindById(UUID roleId);

}
