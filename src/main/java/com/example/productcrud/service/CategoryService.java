package com.example.productcrud.service;

import com.example.productcrud.model.User;
import com.example.productcrud.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import com.example.productcrud.model.Category;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllByUser (User user) {
        return categoryRepository.findByUser(user);
    }

    public Category save(Category category, User user) {
        category.setUser(user);
        return categoryRepository.save(category);
    }

    public Category findByIdAndUser(Long id, User user) {

        return categoryRepository.findByIdAndUser(id, user);
    }
    public void delete(Long id, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user);
        if (category != null) {
            categoryRepository.delete(category);
        }
    }
}