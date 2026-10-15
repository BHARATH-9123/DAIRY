package com.mdairy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customer_bill_items")
public class CustomerBillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonIgnore
    private CustomerBill bill;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "morning_cow_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal morningCowQty = BigDecimal.ZERO;

    @Column(name = "morning_buffalo_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal morningBuffaloQty = BigDecimal.ZERO;

    @Column(name = "night_cow_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal nightCowQty = BigDecimal.ZERO;

    @Column(name = "night_buffalo_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal nightBuffaloQty = BigDecimal.ZERO;

    @Column(name = "daily_total_litres", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyTotalLitres = BigDecimal.ZERO;

    @Column(name = "cow_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal cowRate = BigDecimal.ZERO;

    @Column(name = "buffalo_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal buffaloRate = BigDecimal.ZERO;

    @Column(name = "daily_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal dailyAmount = BigDecimal.ZERO;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerBill getBill() {
        return bill;
    }

    public void setBill(CustomerBill bill) {
        this.bill = bill;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
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

    public BigDecimal getDailyTotalLitres() {
        return dailyTotalLitres;
    }

    public void setDailyTotalLitres(BigDecimal dailyTotalLitres) {
        this.dailyTotalLitres = dailyTotalLitres;
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

    public BigDecimal getDailyAmount() {
        return dailyAmount;
    }

    public void setDailyAmount(BigDecimal dailyAmount) {
        this.dailyAmount = dailyAmount;
    }
}
