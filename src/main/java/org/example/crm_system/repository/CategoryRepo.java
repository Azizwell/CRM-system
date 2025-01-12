package org.example.crm_system.repository;

import org.example.crm_system.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Categories, Long> {


}
