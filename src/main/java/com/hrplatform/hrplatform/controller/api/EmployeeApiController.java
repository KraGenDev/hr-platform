package com.hrplatform.hrplatform.controller.api;

import com.hrplatform.hrplatform.dto.EmployeeDTO;
import com.hrplatform.hrplatform.model.Employee;
import com.hrplatform.hrplatform.model.Position;
import com.hrplatform.hrplatform.model.Department;
import com.hrplatform.hrplatform.repository.EmployeeRepository;
import com.hrplatform.hrplatform.repository.PositionRepository;
import com.hrplatform.hrplatform.repository.DepartmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public List<EmployeeDTO> getAllEmployees() {

        List<EmployeeDTO> employees = new ArrayList<>();
        for (Employee employee : employeeRepository.findAll()) {

            EmployeeDTO employeeDTO = new EmployeeDTO(employee);
            Department department = departmentRepository.findById(employee.getDepartmentId()).orElse(null);
            Position position = positionRepository.findById(employee.getPositionId()).orElse(null);

            employeeDTO.setDepartment(department != null ? department.getName() : "Відсутній");
            employeeDTO.setPosition(position != null ? position.getName() : "Відсутня");
            employees.add(employeeDTO);
        }

        return employees;
    }

    @Operation(summary = "Отримати працівника за ID", description = "Повертає одного працівника за його унікальним ідентифікатором.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Працівника не знайдено."));
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

        validateAndSetStringUserValues(existingEmployee, employee);
        validateAndSetPositionAndDepartmentForUpdate(existingEmployee, employee);

        return employeeRepository.save(existingEmployee);
    }

    @Operation(summary = "Видалити працівника", description = "Видаляє працівника з системи за його ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Працівника з ID " + id + " не знайдено.");
        }
        employeeRepository.deleteById(id);

        return ResponseEntity.ok("Працівника успішно видалено.");
    }

    private void validateAndSetPositionAndDepartment(Employee employee) {
        if (employee.getPositionId() != 0) {
            Position position = positionRepository.findById(employee.getPositionId())
                    .orElseThrow(() -> new RuntimeException("Посаду не знайдено"));
            employee.setPositionId(position != null
                    ? position.getId()
                    : 0);
        }

        if (employee.getDepartmentId() != 0) {
            Department department = departmentRepository.findById(employee.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Відділ не знайдено"));
            employee.setDepartmentId(department != null ? department.getId() : 0);
        }
    }

    private void validateAndSetStringUserValues(Employee employee, Employee updatedEmployee) {
        if(updatedEmployee.getFirstName() != null && !updatedEmployee.getFirstName().equals("string")) {
            employee.setFirstName(updatedEmployee.getFirstName());
        }
        if(updatedEmployee.getLastName() != null && !updatedEmployee.getLastName().equals("string")) {
            employee.setLastName(updatedEmployee.getLastName());
        }
        if(updatedEmployee.getEmail() != null && !updatedEmployee.getEmail().equals("string")) {
            employee.setEmail(updatedEmployee.getEmail());
        }
        if(updatedEmployee.getPhone() != null && !updatedEmployee.getPhone().equals("string")) {
            employee.setPhone(updatedEmployee.getPhone());
        }
    }

    private void validateAndSetPositionAndDepartmentForUpdate(Employee existing, Employee updated) {
        if (updated.getPositionId() != 0) {
            Position position = positionRepository.findById(updated.getPositionId())
                    .orElseThrow(() -> new RuntimeException("Посаду не знайдено"));
            existing.setPositionId(position != null
                    ? position.getId()
                    : 0);
        }

        if (updated.getDepartmentId() != 0) {
            Department department = departmentRepository.findById(updated.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Відділ не знайдено"));
            existing.setDepartmentId(department != null
                    ? department.getId()
                    : 0);
        }
    }
}
