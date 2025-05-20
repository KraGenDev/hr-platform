package com.hrplatform.hrplatform;

import com.hrplatform.hrplatform.controller.api.DepartmentApiController;
import com.hrplatform.hrplatform.model.Department;
import com.hrplatform.hrplatform.repository.DepartmentRepository;
import com.hrplatform.hrplatform.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DepartmentApiControllerUnitTest {

    private DepartmentRepository departmentRepository;
    private EmployeeRepository employeeRepository;
    private DepartmentApiController controller;

    @BeforeEach
    void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        controller = new DepartmentApiController();
        controller.setDepartmentRepository(departmentRepository);
        controller.setEmployeeRepository(employeeRepository);
    }

    @Test
    void testGetAllDepartments_returnsList() {
        Department dep1 = new Department();
        dep1.setId(1L);
        dep1.setName("HR");

        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(dep1));

        List<Department> result = controller.getAllDepartments();

        assertEquals(1, result.size());
        assertEquals("HR", result.get(0).getName());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void testAddDepartment_savesAndReturnsDepartment() {
        Department newDep = new Department();
        newDep.setName("Finance");

        when(departmentRepository.save(newDep)).thenReturn(newDep);

        Department saved = controller.addDepartment(newDep);

        assertEquals("Finance", saved.getName());
        verify(departmentRepository, times(1)).save(newDep);
    }

    @Test
    void testDeleteDepartment_notFound_returns404() {
        Long id = 10L;
        when(departmentRepository.existsById(id)).thenReturn(false);

        ResponseEntity<String> response = controller.deleteDepartment(id);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("не знайдено"));
    }

    @Test
    void testDeleteDepartment_inUse_returnsConflict() {
        Long id = 20L;
        when(departmentRepository.existsById(id)).thenReturn(true);
        when(employeeRepository.existsByDepartmentId(id)).thenReturn(true);

        ResponseEntity<String> response = controller.deleteDepartment(id);

        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("використовується"));
    }

    @Test
    void testDeleteDepartment_success_returnsOk() {
        Long id = 30L;
        when(departmentRepository.existsById(id)).thenReturn(true);
        when(employeeRepository.existsByDepartmentId(id)).thenReturn(false);

        ResponseEntity<String> response = controller.deleteDepartment(id);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("успішно"));
        verify(departmentRepository, times(1)).deleteById(id);
    }
}
