package com.hrplatform.hrplatform.repository;

import com.hrplatform.hrplatform.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Метод для знаходження ролі за її ім'ям
    Role findByName(String name);
}
