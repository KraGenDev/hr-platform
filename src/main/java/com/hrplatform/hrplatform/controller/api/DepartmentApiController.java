package com.hrplatform.hrplatform.controller.api;

import com.hrplatform.hrplatform.model.Department;
import com.hrplatform.hrplatform.repository.DepartmentRepository;
import com.hrplatform.hrplatform.repository.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Department API", description = "Операції з відділами")
@Data
public class DepartmentApiController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Operation(summary = "Отримати всі відділи", description = "Повертає список усіх існуючих відділів у системі.")
    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Operation(summary = "Створити новий відділ", description = "Створення нового відділу та додавання його в БД.")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Department addDepartment(@RequestBody Department department) {
        return departmentRepository.save(department);
    }

    @Operation(summary = "Видалити відділ", description = "Видаляє відділ за вказаним ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        if (!departmentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Відділ з ID " + id + " не знайдено.");
        }

        if (employeeRepository.existsByDepartmentId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Неможливо видалити відділ, оскільки він використовується працівниками.");
        }

        departmentRepository.deleteById(id);
        return ResponseEntity.ok("Відділ успішно видалено.");
    }
}
