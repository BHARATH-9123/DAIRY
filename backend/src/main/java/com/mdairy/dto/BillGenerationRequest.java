package com.mdairy.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BillGenerationRequest {

    @NotNull(message = "Entity ID (Customer or Vendor ID) is required")
    private Long id;

    @NotNull(message = "Billing month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer billingMonth;

    @NotNull(message = "Billing year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    private Integer billingYear;

    private Boolean recalculateDraft = false;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getBillingMonth() { return billingMonth; }
    public void setBillingMonth(Integer billingMonth) { this.billingMonth = billingMonth; }
    public Integer getBillingYear() { return billingYear; }
    public void setBillingYear(Integer billingYear) { this.billingYear = billingYear; }
    public Boolean getRecalculateDraft() { return recalculateDraft; }
    public void setRecalculateDraft(Boolean recalculateDraft) { this.recalculateDraft = recalculateDraft; }
}
