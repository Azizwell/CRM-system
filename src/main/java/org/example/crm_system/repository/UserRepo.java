package org.example.crm_system.repository;

import org.example.crm_system.entity.Role;
import org.example.crm_system.entity.User;
import org.example.crm_system.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
  Optional<User> findByUsername(String username);

  List<User> findAllByRoles(List<Role> roles);

  @Query(value = "select u.id, u.created_at, u.full_name,u.is_enabled,u.username,r.name\n" +
          "from users u\n" +
          "join users_roles ur on u.id = ur.user_id\n" +
          "join role r on ur.roles_id = r.id\n" +
          "order by u.created_at", nativeQuery = true)
  List<UserProjection> queryGetAllUsers();


  @Query(value = "select r.name\n" +
          "from users u\n" +
          "         join users_roles ur on u.id = ur.user_id\n" +
          "         join role r on ur.roles_id = r.id\n" +
          "where username = :username ", nativeQuery = true)
  String rollName(String username);

}
