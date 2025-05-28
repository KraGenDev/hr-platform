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

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees API", description = "Операції з працівниками")
public class EmployeeApiController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;


    @Operation(summary = "Отримати список працівників", description = "Повертає список усіх працівників.")
    @GetMapping("/getAll")
    public List<EmployeeDTO> getAllEmployees() {

        List<EmployeeDTO> employees = new ArrayList<>();
        for (Employee employee : employeeRepository.findAll()) {
            employees.add(createEmployeeDTO(employee));
        }

        return employees;
    }

    @Operation(summary = "Отримати працівника за ID", description = "Повертає одного працівника за його унікальним ідентифікатором.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public EmployeeDTO getEmployeeById(@PathVariable Long id) {
        var employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Працівника не знайдено."));
        return createEmployeeDTO(employee);
    }


    @Operation(summary = "Створити нового працівника", description = "Додає нового працівника в систему.")
    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Employee createEmployee(@RequestBody Employee employee) {
        validateAndSetPositionAndDepartment(employee);
        return employeeRepository.save(employee);
    }

    @Operation(summary = "Оновити працівника", description = "Оновлює дані існуючого працівника за ID.")
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        validateAndSetStringUserValues(existingEmployee, employee);

        try{
            validateAndSetPositionAndDepartmentForUpdate(existingEmployee, employee);
        }
        catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }

        return employeeRepository.save(existingEmployee);
    }

    @Operation(summary = "Видалити працівника", description = "Видаляє працівника з системи за його ID.")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Працівника не знайдено");
        }
        employeeRepository.deleteById(id);

        return ResponseEntity.ok("Працівника успішно видалено.");
    }

    private void validateAndSetPositionAndDepartment(Employee employee) {
        if (employee.getPositionId() != null) {
            Position position = positionRepository.findById(employee.getPositionId())
                    .orElseThrow(() -> new RuntimeException("Посаду не знайдено"));
            employee.setPositionId(position.getId());
        }

        if (employee.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employee.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Відділ не знайдено"));
            employee.setDepartmentId(department.getId());
        }
    }

    private void validateAndSetStringUserValues(Employee employee, Employee updatedEmployee) {
        if(updatedEmployee.getFirstName() != null) {
            employee.setFirstName(updatedEmployee.getFirstName());
        }
        if(updatedEmployee.getLastName() != null) {
            employee.setLastName(updatedEmployee.getLastName());
        }
        if(updatedEmployee.getEmail() != null) {
            employee.setEmail(updatedEmployee.getEmail());
        }
        if(updatedEmployee.getPhone() != null) {
            employee.setPhone(updatedEmployee.getPhone());
        }
    }

    private void validateAndSetPositionAndDepartmentForUpdate(Employee existing, Employee updated) {
        if (updated.getPositionId() != null && updated.getPositionId() != 0) {
            Position position = positionRepository.findById(updated.getPositionId())
                    .orElseThrow(() -> new RuntimeException("Посаду не знайдено"));
            existing.setPositionId(position.getId());
        }

        if (updated.getDepartmentId() != null && updated.getDepartmentId() != 0) {
            Department department = departmentRepository.findById(updated.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Відділ не знайдено"));
            existing.setDepartmentId(department.getId());
        }
    }


    private EmployeeDTO createEmployeeDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO(employee);
        Department department = departmentRepository.findById(employee.getDepartmentId()).orElse(null);
        Position position = positionRepository.findById(employee.getPositionId()).orElse(null);

        employeeDTO.setDepartment(department != null ? department.getName() : "Відсутній");
        employeeDTO.setPosition(position != null ? position.getName() : "Відсутня");

        return employeeDTO;
    }
}
