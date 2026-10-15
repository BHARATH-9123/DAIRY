package com.mdairy.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class VendorAttendanceDto {

    private Long id;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    private String vendorCode;
    private String vendorName;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotBlank(message = "Status is required (PRESENT, ABSENT)")
    private String status = "ABSENT";

    @DecimalMin(value = "0.0", inclusive = true, message = "Morning cow quantity cannot be negative")
    private BigDecimal morningCowQty = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Morning buffalo quantity cannot be negative")
    private BigDecimal morningBuffaloQty = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Night cow quantity cannot be negative")
    private BigDecimal nightCowQty = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Night buffalo quantity cannot be negative")
    private BigDecimal nightBuffaloQty = BigDecimal.ZERO;

    private BigDecimal totalCowQty = BigDecimal.ZERO;
    private BigDecimal totalBuffaloQty = BigDecimal.ZERO;
    private BigDecimal totalLitres = BigDecimal.ZERO;

    private String notes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorCode() { return vendorCode; }
    public void setVendorCode(String vendorCode) { this.vendorCode = vendorCode; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getMorningCowQty() { return morningCowQty; }
    public void setMorningCowQty(BigDecimal morningCowQty) { this.morningCowQty = morningCowQty; }
    public BigDecimal getMorningBuffaloQty() { return morningBuffaloQty; }
    public void setMorningBuffaloQty(BigDecimal morningBuffaloQty) { this.morningBuffaloQty = morningBuffaloQty; }
    public BigDecimal getNightCowQty() { return nightCowQty; }
    public void setNightCowQty(BigDecimal nightCowQty) { this.nightCowQty = nightCowQty; }
    public BigDecimal getNightBuffaloQty() { return nightBuffaloQty; }
    public void setNightBuffaloQty(BigDecimal nightBuffaloQty) { this.nightBuffaloQty = nightBuffaloQty; }
    public BigDecimal getTotalCowQty() { return totalCowQty; }
    public void setTotalCowQty(BigDecimal totalCowQty) { this.totalCowQty = totalCowQty; }
    public BigDecimal getTotalBuffaloQty() { return totalBuffaloQty; }
    public void setTotalBuffaloQty(BigDecimal totalBuffaloQty) { this.totalBuffaloQty = totalBuffaloQty; }
    public BigDecimal getTotalLitres() { return totalLitres; }
    public void setTotalLitres(BigDecimal totalLitres) { this.totalLitres = totalLitres; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
