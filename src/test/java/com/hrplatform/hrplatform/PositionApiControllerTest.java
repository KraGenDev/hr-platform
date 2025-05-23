package com.hrplatform.hrplatform;

import com.hrplatform.hrplatform.controller.api.PositionApiController;
import com.hrplatform.hrplatform.model.Position;
import com.hrplatform.hrplatform.repository.EmployeeRepository;
import com.hrplatform.hrplatform.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PositionApiControllerTest {

    private PositionRepository positionRepository;
    private EmployeeRepository employeeRepository;
    private PositionApiController controller;

    @BeforeEach
    void setUp() {
        positionRepository = mock(PositionRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        controller = new PositionApiController();

        // Вручну вставляємо залежності, бо тестуємо unit (не через Spring)
        controller.setPositionRepository(positionRepository);
        controller.setEmployeeRepository(employeeRepository);
    }

    @Test
    void testListPositions() {
        Position p1 = new Position(1L, "Developer");
        Position p2 = new Position(2L, "Manager");

        when(positionRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Position> result = controller.listPositions();

        assertEquals(2, result.size());
        assertEquals("Developer", result.get(0).getName());
    }

    @Test
    void testAddPosition() {
        Position newPosition = new Position(null, "Tester");
        Position savedPosition = new Position(1L, "Tester");

        when(positionRepository.save(newPosition)).thenReturn(savedPosition);

        Position result = controller.addPosition(newPosition);

        assertEquals(savedPosition.getId(), result.getId());
        assertEquals("Tester", result.getName());
    }

    @Test
    void testDeletePosition_Success() {
        Long id = 1L;

        when(positionRepository.existsById(id)).thenReturn(true);
        when(employeeRepository.existsByPositionId(id)).thenReturn(false);

        assertDoesNotThrow(() -> controller.deletePosition(id));
        verify(positionRepository).deleteById(id);
    }

    @Test
    void testDeletePosition_NotFound() {
        Long id = 2L;

        when(positionRepository.existsById(id)).thenReturn(false);

        ResponseEntity<String> response = controller.deletePosition(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("не знайдено"));
    }


    @Test
    void testDeletePosition_InUse() {
        Long id = 3L;

        when(positionRepository.existsById(id)).thenReturn(true);
        when(employeeRepository.existsByPositionId(id)).thenReturn(true);

        ResponseEntity<String> response = controller.deletePosition(id);

        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("використовується"));
    }
}
