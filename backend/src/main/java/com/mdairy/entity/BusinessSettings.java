package com.mdairy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "business_settings")
public class BusinessSettings {

    @Id
    private Long id = 1L;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName = "M-DAIRY";

    @Column(length = 255)
    private String tagline = "From Farm To Home";

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 50)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(name = "default_cow_selling_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultCowSellingRate = BigDecimal.valueOf(60.00);

    @Column(name = "default_buffalo_selling_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultBuffaloSellingRate = BigDecimal.valueOf(80.00);

    @Column(name = "default_cow_procurement_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultCowProcurementRate = BigDecimal.valueOf(45.00);

    @Column(name = "default_buffalo_procurement_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultBuffaloProcurementRate = BigDecimal.valueOf(65.00);

    @Column(name = "currency_symbol", nullable = false, length = 10)
    private String currencySymbol = "₹";

    @Column(name = "quantity_precision", nullable = false)
    private Integer quantityPrecision = 2;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getDefaultCowSellingRate() {
        return defaultCowSellingRate;
    }

    public void setDefaultCowSellingRate(BigDecimal defaultCowSellingRate) {
        this.defaultCowSellingRate = defaultCowSellingRate;
    }

    public BigDecimal getDefaultBuffaloSellingRate() {
        return defaultBuffaloSellingRate;
    }

    public void setDefaultBuffaloSellingRate(BigDecimal defaultBuffaloSellingRate) {
        this.defaultBuffaloSellingRate = defaultBuffaloSellingRate;
    }

    public BigDecimal getDefaultCowProcurementRate() {
        return defaultCowProcurementRate;
    }

    public void setDefaultCowProcurementRate(BigDecimal defaultCowProcurementRate) {
        this.defaultCowProcurementRate = defaultCowProcurementRate;
    }

    public BigDecimal getDefaultBuffaloProcurementRate() {
        return defaultBuffaloProcurementRate;
    }

    public void setDefaultBuffaloProcurementRate(BigDecimal defaultBuffaloProcurementRate) {
        this.defaultBuffaloProcurementRate = defaultBuffaloProcurementRate;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public Integer getQuantityPrecision() {
        return quantityPrecision;
    }

    public void setQuantityPrecision(Integer quantityPrecision) {
        this.quantityPrecision = quantityPrecision;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
