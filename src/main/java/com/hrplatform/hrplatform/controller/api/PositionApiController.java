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
    @GetMapping("/getAll")
    public List<Position> listPositions() {
        return positionRepository.findAll();
    }

    @Operation(summary = "Додати нову посаду", description = "Створює нову посаду та зберігає її в базу даних.")
    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Position addPosition(@RequestBody Position position) {
        return positionRepository.save(position);
    }


    @Operation(summary = "Видалити посаду", description = "Видаляє посаду за вказаним ID.")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deletePosition(@PathVariable Long id) {
        if (!positionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Посаду з ID " + id + " не знайдено");
        }

        if (employeeRepository.existsByPositionId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Неможливо видалити посаду, оскільки вона використовується працівниками.");
        }

        positionRepository.deleteById(id);
        return ResponseEntity.ok("Посаду успішно видалено.");
    }

}
