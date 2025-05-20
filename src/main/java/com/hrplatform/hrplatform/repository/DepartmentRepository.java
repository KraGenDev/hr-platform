package com.hrplatform.hrplatform.repository;

import com.hrplatform.hrplatform.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
