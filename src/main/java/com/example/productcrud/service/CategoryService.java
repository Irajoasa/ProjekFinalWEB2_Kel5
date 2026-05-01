package com.example.productcrud.service;

import com.example.productcrud.model.User;
import com.example.productcrud.repository.CategoryRepository;
import com.example.productcrud.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {

        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> findAllByUser (User user) {
        return categoryRepository.findByUser(user);
    }

    public Category save(Category category, User user) {
        String name = category.getName() == null ? "" : category.getName().trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Nama kategori wajib diisi.");
        }

        Category categoryToSave = category;
        if (category.getId() != null) {
            categoryToSave = categoryRepository.findByIdAndUser(category.getId(), user);
            if (categoryToSave == null) {
                throw new IllegalArgumentException("Kategori tidak ditemukan.");
            }
        }

        boolean duplicateName = category.getId() == null
                ? categoryRepository.existsByUserAndNameIgnoreCase(user, name)
                : categoryRepository.existsByUserAndNameIgnoreCaseAndIdNot(user, name, category.getId());
        if (duplicateName) {
            throw new IllegalArgumentException("Nama kategori sudah digunakan.");
        }

        categoryToSave.setName(name);
        categoryToSave.setDescription(category.getDescription());
        categoryToSave.setUser(user);
        return categoryRepository.save(categoryToSave);
    }

    public Category findByIdAndUser(Long id, User user) {

        return categoryRepository.findByIdAndUser(id, user);
    }
    @Transactional
    public void delete(Long id, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user);
        if (category != null) {
            List<Product> products = productRepository.findAllByOwnerAndCategory(user, category);
            products.forEach(product -> product.setCategory(null));
            productRepository.saveAll(products);
            categoryRepository.delete(category);
        }
    }
}
