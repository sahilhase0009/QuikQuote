package com.quoteflow.repository;

import com.quoteflow.entity.Quotation;
import com.quoteflow.entity.QuotationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findByBusinessIdOrderByCreatedAtDesc(Long businessId);

    List<Quotation> findByBusinessIdAndStatusOrderByCreatedAtDesc(Long businessId, QuotationStatus status);

    Optional<Quotation> findByIdAndBusinessId(Long id, Long businessId);

    Optional<Quotation> findByBusinessIdAndQuotationNumber(Long businessId, String quotationNumber);

    Optional<Quotation> findTopByBusinessIdOrderByCreatedAtDesc(Long businessId);

    long countByBusinessId(Long businessId);

    long countByBusinessIdAndStatus(Long businessId, QuotationStatus status);

    @Query("SELECT COALESCE(SUM(q.grandTotal), 0) FROM Quotation q WHERE q.business.id = :businessId")
    BigDecimal sumGrandTotalByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT COALESCE(SUM(q.grandTotal), 0) FROM Quotation q WHERE q.business.id = :businessId AND q.status = :status")
    BigDecimal sumGrandTotalByBusinessIdAndStatus(@Param("businessId") Long businessId, @Param("status") QuotationStatus status);

    @Query("SELECT COALESCE(SUM(q.grandTotal), 0) FROM Quotation q WHERE q.business.id = :businessId AND q.status IN ('DRAFT', 'SENT', 'VIEWED')")
    BigDecimal sumPendingGrandTotalByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT q FROM Quotation q WHERE q.business.id = :businessId AND " +
           "(LOWER(q.quotationNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(q.customer.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(q.customer.companyName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Quotation> searchQuotations(@Param("businessId") Long businessId, @Param("query") String query);

    List<Quotation> findTop5ByBusinessIdOrderByCreatedAtDesc(Long businessId);
}
