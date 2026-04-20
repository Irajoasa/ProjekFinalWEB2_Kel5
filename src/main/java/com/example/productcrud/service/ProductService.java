package com.example.productcrud.service;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.ProductRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> findAllByOwnerAndFilters(User owner, String keyword, Category category, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = category != null;

        if (hasKeyword && hasCategory) {
            return productRepository.findByOwnerAndNameContainingIgnoreCaseAndCategory(owner, keyword.trim(), category, pageable);
        }
        if (hasKeyword) {
            return productRepository.findByOwnerAndNameContainingIgnoreCase(owner, keyword.trim(), pageable);
        }
        if (hasCategory) {
            return productRepository.findByOwnerAndCategory(owner, category, pageable);
        }
        return productRepository.findByOwner(owner, pageable);
    }

    public Optional<Product> findByIdAndOwner(Long id, User owner) {
        return productRepository.findByIdAndOwner(id, owner);
    }

    public long countByOwner(User owner) {
        return productRepository.countByOwner(owner);
    }

    public long countActiveByOwner(User owner) {
        return productRepository.countByOwnerAndActive(owner, true);
    }

    public long countInactiveByOwner(User owner) {
        return productRepository.countByOwnerAndActive(owner, false);
    }

    public long sumInventoryValueByOwner(User owner) {
        Long total = productRepository.sumInventoryValueByOwner(owner);
        return total == null ? 0L : total;
    }

    public List<ProductRepository.CategoryCountProjection> countProductsByCategory(User owner) {
        return productRepository.countProductsByCategory(owner);
    }

    public List<Product> findLowStockProducts(User owner) {
        return productRepository.findByOwnerAndStockLessThanOrderByStockAscNameAsc(owner, 5);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteByIdAndOwner(Long id, User owner) {
        productRepository.findByIdAndOwner(id, owner)
                .ifPresent(product -> productRepository.delete(product));
    }
}
