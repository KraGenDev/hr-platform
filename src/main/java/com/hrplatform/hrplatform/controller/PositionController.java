package com.hrplatform.hrplatform.controller;

import com.hrplatform.hrplatform.model.Position;
import com.hrplatform.hrplatform.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/positions")
public class PositionController {

    @Autowired
    private PositionRepository positionRepository;

    @GetMapping
    public String listPositions(Model model) {
        model.addAttribute("positions", positionRepository.findAll());  // Отримуємо список посад
        model.addAttribute("position", new Position());  // Додаємо об'єкт для форми
        model.addAttribute("body", "positions");  // Додаємо частину для списку посад
        return "index";  // Вказуємо головний шаблон для відображення
    }

    @PostMapping("/add")
    public String addPosition(@ModelAttribute Position position) {
        positionRepository.save(position);  // Зберігаємо нову посаду
        return "redirect:/positions";  // Повертаємося на сторінку зі списком посад
    }

    @PostMapping("/delete/{id}")
    public String deletePosition(@PathVariable Long id) {
        positionRepository.deleteById(id);  // Видаляємо посаду по id
        return "redirect:/positions";  // Повертаємося на сторінку зі списком посад
    }
}
