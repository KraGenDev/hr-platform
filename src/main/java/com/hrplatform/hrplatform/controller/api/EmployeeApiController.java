package com.hrplatform.hrplatform.controller.api;

import com.hrplatform.hrplatform.model.Employee;
import com.hrplatform.hrplatform.model.Position;
import com.hrplatform.hrplatform.model.Department;
import com.hrplatform.hrplatform.repository.EmployeeRepository;
import com.hrplatform.hrplatform.repository.PositionRepository;
import com.hrplatform.hrplatform.repository.DepartmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Employees API", description = "Операції з працівниками")
public class EmployeeApiController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;


    @Operation(summary = "Отримати список працівників", description = "Повертає список усіх працівників.")
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Operation(summary = "Отримати працівника за ID", description = "Повертає одного працівника за його унікальним ідентифікатором.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }


    @Operation(summary = "Створити нового працівника", description = "Додає нового працівника в систему.")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Employee createEmployee(@RequestBody Employee employee) {
        validateAndSetPositionAndDepartment(employee);
        return employeeRepository.save(employee);
    }

    @Operation(summary = "Оновити працівника", description = "Оновлює дані існуючого працівника за ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setPhone(employee.getPhone());

        validateAndSetPositionAndDepartmentForUpdate(existingEmployee, employee);

        return employeeRepository.save(existingEmployee);
    }

    @Operation(summary = "Видалити працівника", description = "Видаляє працівника з системи за його ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public void deleteEmployee(@PathVariable Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    private void validateAndSetPositionAndDepartment(Employee employee) {
        if (employee.getPosition() != null && employee.getPosition().getId() != null) {
            Position position = positionRepository.findById(employee.getPosition().getId())
                    .orElseThrow(() -> new RuntimeException("Position not found"));
            employee.setPosition(position);
        }

        if (employee.getDepartment() != null && employee.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(employee.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(department);
        }
    }

    private void validateAndSetPositionAndDepartmentForUpdate(Employee existing, Employee updated) {
        if (updated.getPosition() != null && updated.getPosition().getId() != null) {
            Position position = positionRepository.findById(updated.getPosition().getId())
                    .orElseThrow(() -> new RuntimeException("Position not found"));
            existing.setPosition(position);
        }

        if (updated.getDepartment() != null && updated.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(updated.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            existing.setDepartment(department);
        }
    }
}
