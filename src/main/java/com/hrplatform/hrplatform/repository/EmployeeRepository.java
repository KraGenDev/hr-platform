package com.hrplatform.hrplatform.repository;

import com.hrplatform.hrplatform.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByPositionId(Long positionId);
    boolean existsByDepartmentId(Long departmentId);

    @Query("SELECT e FROM Employee e " +
            "LEFT JOIN Position p ON e.positionId = p.id " +
            "LEFT JOIN Department d ON e.departmentId = d.id " +
            "WHERE " +
            "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")

    List<Employee> searchAllFields(@Param("keyword") String keyword);
}
