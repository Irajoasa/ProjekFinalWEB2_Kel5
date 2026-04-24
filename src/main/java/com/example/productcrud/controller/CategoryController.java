package com.example.productcrud.controller;

import com.example.productcrud.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.productcrud.model.Category;
@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    // LIST
    @GetMapping
    public String list(Model model){
        model.addAttribute("categories",
                categoryService.findAll());
        return "category/list";
    }

    // FORM CREATE
    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("category",
                new Category());
        return "category/form";
    }

    // SAVE
    @PostMapping("/save")
    public String save(@ModelAttribute Category category){
        categoryService.save(category);
        return "redirect:/categories";
    }

    // EDIT
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model){
        model.addAttribute("category",
                categoryService.findById(id));
        return "category/form";
    }

    // DELETE
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id){
        categoryService.delete(id);
        return "redirect:/categories";
    }
}