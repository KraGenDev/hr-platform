package com.hrplatform.hrplatform.controller;

import com.hrplatform.hrplatform.model.Employee;
import com.hrplatform.hrplatform.repository.DepartmentRepository;
import com.hrplatform.hrplatform.repository.EmployeeRepository;
import com.hrplatform.hrplatform.repository.PositionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeWebController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    // Головна сторінка - список працівників
    @GetMapping
    public String getAllEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("page", "employees");
        model.addAttribute("body", "employee-list");
        return "index";  // Головний шаблон
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("positions", positionRepository.findAll());  // додаємо список посад
        model.addAttribute("departments", departmentRepository.findAll()); // додаємо список відділів
        model.addAttribute("body", "employee-form");  // Додаємо контент для форми
        return "index"; // Повертає головний шаблон
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        model.addAttribute("employee", employee);
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("body", "edit-employee");
        return "index";  // Повертає головний шаблон
    }

    @PostMapping("/new")
    public String createEmployee(@ModelAttribute Employee employee) {
        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @PostMapping("/edit/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") Employee updatedEmployee,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("positions", positionRepository.findAll());
            model.addAttribute("departments", departmentRepository.findAll());
            model.addAttribute("body", "employee-form");
            return "index";
        }

        // Завантажуємо наявного працівника з бази
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Оновлюємо тільки поля, які змінюються
        existingEmployee.setFirstName(updatedEmployee.getFirstName());
        existingEmployee.setLastName(updatedEmployee.getLastName());
        existingEmployee.setEmail(updatedEmployee.getEmail());
        existingEmployee.setPhone(updatedEmployee.getPhone());
        existingEmployee.setPosition(updatedEmployee.getPosition());
        existingEmployee.setDepartment(updatedEmployee.getDepartment());

        // Зберігаємо зміни
        employeeRepository.save(existingEmployee);

        return "redirect:/employees"; // Повертаємо на список працівників
    }



    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }
}

