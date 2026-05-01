package com.example.productcrud.controller;

import com.example.productcrud.model.User;
import com.example.productcrud.repository.CategoryRepository;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.productcrud.model.Category;
import org.springframework.security.core.Authentication;
@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public CategoryController(CategoryService categoryService, UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }
    private User getCurrentUser (Authentication authentication) {
        String username = authentication.getName(); // Ambil nama pengguna yang sedang login
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));
    }


    @GetMapping
    public String list(Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        model.addAttribute("categories" , categoryService.findAllByUser(currentUser));
        return "category/list";
    }


    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("category",
                new Category());
        return "category/form";
    }


    @PostMapping("/save")
    public String save(@ModelAttribute Category category, Authentication authentication){
        User currentUser = getCurrentUser(authentication);
        categoryService.save(category, currentUser);
        return "redirect:/categories";
    }


    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model, Authentication authentication){
        User currentUser = getCurrentUser(authentication);
        Category category = categoryService.findByIdAndUser(id, currentUser);

            if (category == null) {
                return "redirect:/categories";
            }

            model.addAttribute("category", category);
            return "category/form";
    }

        @GetMapping("/{id}/delete")
        public String delete(@PathVariable Long id, Authentication authentication) {
            User currentUser = getCurrentUser(authentication);
            categoryService.delete(id, currentUser);
            return "redirect:/categories";
        }
}