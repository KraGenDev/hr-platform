package com.hrplatform.hrplatform.controller.api;

import com.hrplatform.hrplatform.model.Position;
import com.hrplatform.hrplatform.repository.EmployeeRepository;
import com.hrplatform.hrplatform.repository.PositionRepository;
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
@RequestMapping("/api/positions")
@Tag(name = "Position API", description = "Операції з посадами")
@Data
public class PositionApiController {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Operation(summary = "Отримати всі посади", description = "Повертає список усіх посад у системі.")
    @GetMapping
    public List<Position> listPositions() {
        return positionRepository.findAll();
    }

    @Operation(summary = "Додати нову посаду", description = "Створює нову посаду та зберігає її в базу даних.")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Position addPosition(@RequestBody Position position) {
        return positionRepository.save(position);
    }

//    @Operation(summary = "Видалити посаду", description = "Видаляє посаду за вказаним ID.")
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public void deletePosition(@PathVariable Long id) {
//        if (!positionRepository.existsById(id)) {
//            throw new RuntimeException("Position not found");
//        }
//
//        boolean isInUse = employeeRepository.existsByPositionId(id);
//        if (isInUse) {
//            throw new RuntimeException("Cannot delete position: it is used by employees");
//        }
//
//        positionRepository.deleteById(id);
//
//    }

    @Operation(summary = "Видалити посаду", description = "Видаляє посаду за вказаним ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        if (!positionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Посада з ID " + id + " не знайдена.");
        }

        if (employeeRepository.existsByDepartmentId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Неможливо видалити посаду, оскільки вона використовується працівниками.");
        }

        positionRepository.deleteById(id);
        return ResponseEntity.ok("Посаду успішно видалено.");
    }

}
