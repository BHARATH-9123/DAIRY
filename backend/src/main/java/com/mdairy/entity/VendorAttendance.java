package com.mdairy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_attendance", uniqueConstraints = {
    @UniqueConstraint(name = "uk_vendor_attendance_date", columnNames = {"vendor_id", "attendance_date"})
})
public class VendorAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false, length = 20)
    private String status = "ABSENT"; // PRESENT, ABSENT

    @Column(name = "morning_cow_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal morningCowQty = BigDecimal.ZERO;

    @Column(name = "morning_buffalo_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal morningBuffaloQty = BigDecimal.ZERO;

    @Column(name = "night_cow_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal nightCowQty = BigDecimal.ZERO;

    @Column(name = "night_buffalo_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal nightBuffaloQty = BigDecimal.ZERO;

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        sanitizeQuantities();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        sanitizeQuantities();
    }

    public void sanitizeQuantities() {
        if ("ABSENT".equalsIgnoreCase(status)) {
            // Absent vendor attendance contributes zero billable collection
            this.morningCowQty = BigDecimal.ZERO;
            this.morningBuffaloQty = BigDecimal.ZERO;
            this.nightCowQty = BigDecimal.ZERO;
            this.nightBuffaloQty = BigDecimal.ZERO;
        } else {
            if (this.morningCowQty == null) this.morningCowQty = BigDecimal.ZERO;
            if (this.morningBuffaloQty == null) this.morningBuffaloQty = BigDecimal.ZERO;
            if (this.nightCowQty == null) this.nightCowQty = BigDecimal.ZERO;
            if (this.nightBuffaloQty == null) this.nightBuffaloQty = BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalCowQty() {
        if ("ABSENT".equalsIgnoreCase(status)) return BigDecimal.ZERO;
        BigDecimal m = morningCowQty != null ? morningCowQty : BigDecimal.ZERO;
        BigDecimal n = nightCowQty != null ? nightCowQty : BigDecimal.ZERO;
        return m.add(n);
    }

    public BigDecimal getTotalBuffaloQty() {
        if ("ABSENT".equalsIgnoreCase(status)) return BigDecimal.ZERO;
        BigDecimal m = morningBuffaloQty != null ? morningBuffaloQty : BigDecimal.ZERO;
        BigDecimal n = nightBuffaloQty != null ? nightBuffaloQty : BigDecimal.ZERO;
        return m.add(n);
    }

    public BigDecimal getTotalLitres() {
        return getTotalCowQty().add(getTotalBuffaloQty());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
