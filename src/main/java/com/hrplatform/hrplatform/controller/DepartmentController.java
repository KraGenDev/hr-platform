package com.hrplatform.hrplatform.controller;

import com.hrplatform.hrplatform.model.Department;
import com.hrplatform.hrplatform.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("department", new Department());
        model.addAttribute("body", "departments");  // Додаємо частину для списку відділів
        return "index";  // Вказуємо головний шаблон для відображення
    }

    @PostMapping("/add")
    public String addDepartment(@ModelAttribute Department department) {
        departmentRepository.save(department);
        return "redirect:/department";  // Повертає до списку відділів
    }

    @PostMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentRepository.deleteById(id);
        return "redirect:/department";  // Повертає до списку відділів
    }
}
