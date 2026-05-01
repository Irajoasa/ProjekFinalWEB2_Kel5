package com.example.productcrud.repository;

import com.example.productcrud.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.productcrud.model.User;
import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);
    Category findByIdAndUser(Long id, User user);
    boolean existsByUserAndNameIgnoreCase(User user, String name);
    boolean existsByUserAndNameIgnoreCaseAndIdNot(User user, String name, Long id);
}
