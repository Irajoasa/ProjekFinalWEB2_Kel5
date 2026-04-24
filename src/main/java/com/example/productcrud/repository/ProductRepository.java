package com.example.productcrud.repository;

import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.productcrud.model.Category;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    interface CategoryCountProjection {
        Category getCategory();
        long getTotal();
    }

    Page<Product> findByOwner(User owner, Pageable pageable);

    Page<Product> findByOwnerAndNameContainingIgnoreCase(User owner, String name, Pageable pageable);

    Page<Product> findByOwnerAndCategory(User owner, Category category, Pageable pageable);

    Page<Product> findByOwnerAndNameContainingIgnoreCaseAndCategory(User owner, String name, Category category, Pageable pageable);

    long countByOwner(User owner);

    long countByOwnerAndActive(User owner, boolean active);

    List<Product> findByOwnerAndStockLessThanOrderByStockAscNameAsc(User owner, int stock);

    @Query("select coalesce(sum(p.price * p.stock), 0) from Product p where p.owner = :owner")
    Long sumInventoryValueByOwner(User owner);

    @Query("""
            select p.category as category, count(p) as total
            from Product p
            where p.owner = :owner
            group by p.category
            order by count(p) desc, p.category asc
            """)
    List<CategoryCountProjection> countProductsByCategory(User owner);

    Optional<Product> findByIdAndOwner(Long id, User owner);
}
