package com.mdairy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code", unique = true, nullable = false, length = 50)
    private String customerCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "milk_type", nullable = false, length = 20)
    private String milkType = "COW"; // COW, BUFFALO, BOTH

    @Column(name = "delivery_schedule", nullable = false, length = 20)
    private String deliverySchedule = "MORNING"; // MORNING, NIGHT, BOTH

    @Column(name = "morning_cow_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal morningCowQty = BigDecimal.ZERO;

    @Column(name = "morning_buffalo_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal morningBuffaloQty = BigDecimal.ZERO;

    @Column(name = "night_cow_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal nightCowQty = BigDecimal.ZERO;

    @Column(name = "night_buffalo_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal nightBuffaloQty = BigDecimal.ZERO;

    @Column(name = "cow_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal cowRate = BigDecimal.valueOf(60.00);

    @Column(name = "buffalo_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal buffaloRate = BigDecimal.valueOf(80.00);

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (morningCowQty == null) morningCowQty = BigDecimal.ZERO;
        if (morningBuffaloQty == null) morningBuffaloQty = BigDecimal.ZERO;
        if (nightCowQty == null) nightCowQty = BigDecimal.ZERO;
        if (nightBuffaloQty == null) nightBuffaloQty = BigDecimal.ZERO;
        if (active == null) active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMilkType() {
        return milkType;
    }

    public void setMilkType(String milkType) {
        this.milkType = milkType;
    }

    public String getDeliverySchedule() {
        return deliverySchedule;
    }

    public void setDeliverySchedule(String deliverySchedule) {
        this.deliverySchedule = deliverySchedule;
    }

    public BigDecimal getMorningCowQty() {
        return morningCowQty;
    }

    public void setMorningCowQty(BigDecimal morningCowQty) {
        this.morningCowQty = morningCowQty;
    }

    public BigDecimal getMorningBuffaloQty() {
        return morningBuffaloQty;
    }

    public void setMorningBuffaloQty(BigDecimal morningBuffaloQty) {
        this.morningBuffaloQty = morningBuffaloQty;
    }

    public BigDecimal getNightCowQty() {
        return nightCowQty;
    }

    public void setNightCowQty(BigDecimal nightCowQty) {
        this.nightCowQty = nightCowQty;
    }

    public BigDecimal getNightBuffaloQty() {
        return nightBuffaloQty;
    }

    public void setNightBuffaloQty(BigDecimal nightBuffaloQty) {
        this.nightBuffaloQty = nightBuffaloQty;
    }

    public BigDecimal getCowRate() {
        return cowRate;
    }

    public void setCowRate(BigDecimal cowRate) {
        this.cowRate = cowRate;
    }

    public BigDecimal getBuffaloRate() {
        return buffaloRate;
    }

    public void setBuffaloRate(BigDecimal buffaloRate) {
        this.buffaloRate = buffaloRate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
