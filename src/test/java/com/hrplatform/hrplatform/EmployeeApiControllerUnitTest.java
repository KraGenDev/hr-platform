package com.hrplatform.hrplatform;

import com.hrplatform.hrplatform.controller.api.EmployeeApiController;
import com.hrplatform.hrplatform.dto.EmployeeDTO;
import com.hrplatform.hrplatform.model.Employee;
import com.hrplatform.hrplatform.model.Position;
import com.hrplatform.hrplatform.model.Department;
import com.hrplatform.hrplatform.repository.EmployeeRepository;
import com.hrplatform.hrplatform.repository.PositionRepository;
import com.hrplatform.hrplatform.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeeApiControllerUnitTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeApiController employeeApiController;

    private Employee employee;
    private Position position;
    private Department department;

    @BeforeEach
    void setUp() {
        position = new Position();
        position.setId(1L);
        position.setName("Developer");

        department = new Department();
        department.setId(1L);
        department.setName("IT");

        employee = new Employee();
        employee.setFirstName("Test");
        employee.setLastName("User");
        employee.setEmail("test@example.com");
        employee.setPhone("123456789");
        employee.setPositionId(position.getId());
        employee.setDepartmentId(department.getId());
    }

    @Test
    void testCreateEmployee() {
        // Arrange
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        Employee createdEmployee = employeeApiController.createEmployee(employee);

        // Assert
        assertNotNull(createdEmployee);
        assertEquals("Test", createdEmployee.getFirstName());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testCreateEmployee_withInvalidPosition() {
        when(positionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeApiController.createEmployee(employee));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testCreateEmployee_withInvalidDepartment() {
        // Arrange
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> employeeApiController.createEmployee(employee));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testGetAllEmployees() {
        // Arrange
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        // Act
        var employees = employeeApiController.getAllEmployees();

        // Assert
        assertNotNull(employees);
        assertTrue(employees.size() > 0);
    }

    @Test
    void testGetEmployeeById() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        // Act
        EmployeeDTO foundEmployee = employeeApiController.getEmployeeById(1L);

        // Assert
        assertNotNull(foundEmployee);
        assertEquals("Test", foundEmployee.getFirstName());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> employeeApiController.getEmployeeById(1L));
    }

    @Test
    void testUpdateEmployee() {
        // Arrange
        Employee updatedEmployee = new Employee();
        updatedEmployee.setFirstName("Updated");
        updatedEmployee.setLastName("User");
        updatedEmployee.setEmail("updated@example.com");
        updatedEmployee.setPhone("987654321");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        Employee result = employeeApiController.updateEmployee(1L, updatedEmployee);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_NotFound() {
        // Arrange
        Employee updatedEmployee = new Employee();
        updatedEmployee.setFirstName("Updated");
        updatedEmployee.setLastName("User");

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> employeeApiController.updateEmployee(1L, updatedEmployee));
    }

    @Test
    void testDeleteEmployee() {
        // Arrange
        when(employeeRepository.existsById(1L)).thenReturn(true);

        // Act
        employeeApiController.deleteEmployee(1L);

        // Assert
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteEmployee_NotFound() {

        when(employeeRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<String> response =employeeApiController.deleteEmployee(1L);

        assertTrue(response.getBody().contains("не знайдено"));
    }
}
