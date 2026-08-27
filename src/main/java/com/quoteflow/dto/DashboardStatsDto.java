package com.quoteflow.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDto {

    private long totalQuotations;
    private long draftCount;
    private long sentCount;
    private long acceptedCount;
    private long rejectedCount;

    private BigDecimal totalValue = BigDecimal.ZERO;
    private BigDecimal acceptedValue = BigDecimal.ZERO;
    private BigDecimal pendingValue = BigDecimal.ZERO;

    private List<QuotationDto> recentQuotations = new ArrayList<>();

    public DashboardStatsDto() {}

    public long getTotalQuotations() {
        return totalQuotations;
    }

    public void setTotalQuotations(long totalQuotations) {
        this.totalQuotations = totalQuotations;
    }

    public long getDraftCount() {
        return draftCount;
    }

    public void setDraftCount(long draftCount) {
        this.draftCount = draftCount;
    }

    public long getSentCount() {
        return sentCount;
    }

    public void setSentCount(long sentCount) {
        this.sentCount = sentCount;
    }

    public long getAcceptedCount() {
        return acceptedCount;
    }

    public void setAcceptedCount(long acceptedCount) {
        this.acceptedCount = acceptedCount;
    }

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getAcceptedValue() {
        return acceptedValue;
    }

    public void setAcceptedValue(BigDecimal acceptedValue) {
        this.acceptedValue = acceptedValue;
    }

    public BigDecimal getPendingValue() {
        return pendingValue;
    }

    public void setPendingValue(BigDecimal pendingValue) {
        this.pendingValue = pendingValue;
    }

    public List<QuotationDto> getRecentQuotations() {
        return recentQuotations;
    }

    public void setRecentQuotations(List<QuotationDto> recentQuotations) {
        this.recentQuotations = recentQuotations;
    }
}
