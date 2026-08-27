package com.quoteflow.repository;

import com.quoteflow.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    List<Customer> findByBusinessIdOrderByCreatedAtDesc(Long businessId);

    Optional<Customer> findByIdAndBusinessId(Long id, Long businessId);

    @Query("SELECT c FROM Customer c WHERE c.business.id = :businessId AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Customer> searchCustomers(@Param("businessId") Long businessId, @Param("query") String query);
}
