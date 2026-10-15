package com.mdairy.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerDto {

    private Long id;

    @NotBlank(message = "Customer code is required")
    private String customerCode;

    @NotBlank(message = "Customer name is required")
    private String name;

    private String phone;
    private String address;

    @NotBlank(message = "Milk type is required (COW, BUFFALO, BOTH)")
    private String milkType = "COW";

    @NotBlank(message = "Delivery schedule is required (MORNING, NIGHT, BOTH)")
    private String deliverySchedule = "MORNING";

    @DecimalMin(value = "0.0", inclusive = true, message = "Morning cow quantity cannot be negative")
    private BigDecimal morningCowQty = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Morning buffalo quantity cannot be negative")
    private BigDecimal morningBuffaloQty = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Night cow quantity cannot be negative")
    private BigDecimal nightCowQty = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Night buffalo quantity cannot be negative")
    private BigDecimal nightBuffaloQty = BigDecimal.ZERO;

    @NotNull(message = "Cow rate is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cow rate cannot be negative")
    private BigDecimal cowRate = BigDecimal.valueOf(60.00);

    @NotNull(message = "Buffalo rate is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Buffalo rate cannot be negative")
    private BigDecimal buffaloRate = BigDecimal.valueOf(80.00);

    private String notes;
    private Boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMilkType() { return milkType; }
    public void setMilkType(String milkType) { this.milkType = milkType; }
    public String getDeliverySchedule() { return deliverySchedule; }
    public void setDeliverySchedule(String deliverySchedule) { this.deliverySchedule = deliverySchedule; }
    public BigDecimal getMorningCowQty() { return morningCowQty; }
    public void setMorningCowQty(BigDecimal morningCowQty) { this.morningCowQty = morningCowQty; }
    public BigDecimal getMorningBuffaloQty() { return morningBuffaloQty; }
    public void setMorningBuffaloQty(BigDecimal morningBuffaloQty) { this.morningBuffaloQty = morningBuffaloQty; }
    public BigDecimal getNightCowQty() { return nightCowQty; }
    public void setNightCowQty(BigDecimal nightCowQty) { this.nightCowQty = nightCowQty; }
    public BigDecimal getNightBuffaloQty() { return nightBuffaloQty; }
    public void setNightBuffaloQty(BigDecimal nightBuffaloQty) { this.nightBuffaloQty = nightBuffaloQty; }
    public BigDecimal getCowRate() { return cowRate; }
    public void setCowRate(BigDecimal cowRate) { this.cowRate = cowRate; }
    public BigDecimal getBuffaloRate() { return buffaloRate; }
    public void setBuffaloRate(BigDecimal buffaloRate) { this.buffaloRate = buffaloRate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
