package com.mdairy.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BusinessSettingsDto {

    private Long id;

    @NotBlank(message = "Business name is required")
    private String businessName;

    private String tagline;
    private String address;
    private String phone;
    private String email;

    @NotNull(message = "Default cow selling rate is required")
    @DecimalMin(value = "0.0", message = "Rate cannot be negative")
    private BigDecimal defaultCowSellingRate;

    @NotNull(message = "Default buffalo selling rate is required")
    @DecimalMin(value = "0.0", message = "Rate cannot be negative")
    private BigDecimal defaultBuffaloSellingRate;

    @NotNull(message = "Default cow procurement rate is required")
    @DecimalMin(value = "0.0", message = "Rate cannot be negative")
    private BigDecimal defaultCowProcurementRate;

    @NotNull(message = "Default buffalo procurement rate is required")
    @DecimalMin(value = "0.0", message = "Rate cannot be negative")
    private BigDecimal defaultBuffaloProcurementRate;

    private String currencySymbol = "₹";
    private Integer quantityPrecision = 2;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getDefaultCowSellingRate() { return defaultCowSellingRate; }
    public void setDefaultCowSellingRate(BigDecimal defaultCowSellingRate) { this.defaultCowSellingRate = defaultCowSellingRate; }
    public BigDecimal getDefaultBuffaloSellingRate() { return defaultBuffaloSellingRate; }
    public void setDefaultBuffaloSellingRate(BigDecimal defaultBuffaloSellingRate) { this.defaultBuffaloSellingRate = defaultBuffaloSellingRate; }
    public BigDecimal getDefaultCowProcurementRate() { return defaultCowProcurementRate; }
    public void setDefaultCowProcurementRate(BigDecimal defaultCowProcurementRate) { this.defaultCowProcurementRate = defaultCowProcurementRate; }
    public BigDecimal getDefaultBuffaloProcurementRate() { return defaultBuffaloProcurementRate; }
    public void setDefaultBuffaloProcurementRate(BigDecimal defaultBuffaloProcurementRate) { this.defaultBuffaloProcurementRate = defaultBuffaloProcurementRate; }
    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }
    public Integer getQuantityPrecision() { return quantityPrecision; }
    public void setQuantityPrecision(Integer quantityPrecision) { this.quantityPrecision = quantityPrecision; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
