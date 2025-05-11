package com.hrplatform.hrplatform.repository;

import com.hrplatform.hrplatform.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByPositionId(Long positionId);
    boolean existsByDepartmentId(Long departmentId);
}
