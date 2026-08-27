package com.quoteflow.repository;

import com.quoteflow.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByBusinessIdOrderByCreatedAtDesc(Long businessId);

    List<Product> findByBusinessIdAndActiveTrueOrderByNameAsc(Long businessId);

    Optional<Product> findByIdAndBusinessId(Long id, Long businessId);

    @Query("SELECT p FROM Product p WHERE p.business.id = :businessId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchProducts(@Param("businessId") Long businessId, @Param("query") String query);
}
