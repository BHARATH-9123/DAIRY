package com.mdairy.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VendorBillItemDto {

    private Long id;
    private LocalDate attendanceDate;
    private BigDecimal morningCowQty;
    private BigDecimal morningBuffaloQty;
    private BigDecimal nightCowQty;
    private BigDecimal nightBuffaloQty;
    private BigDecimal dailyTotalLitres;
    private BigDecimal cowRate;
    private BigDecimal buffaloRate;
    private BigDecimal dailyAmount;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    public BigDecimal getMorningCowQty() { return morningCowQty; }
    public void setMorningCowQty(BigDecimal morningCowQty) { this.morningCowQty = morningCowQty; }
    public BigDecimal getMorningBuffaloQty() { return morningBuffaloQty; }
    public void setMorningBuffaloQty(BigDecimal morningBuffaloQty) { this.morningBuffaloQty = morningBuffaloQty; }
    public BigDecimal getNightCowQty() { return nightCowQty; }
    public void setNightCowQty(BigDecimal nightCowQty) { this.nightCowQty = nightCowQty; }
    public BigDecimal getNightBuffaloQty() { return nightBuffaloQty; }
    public void setNightBuffaloQty(BigDecimal nightBuffaloQty) { this.nightBuffaloQty = nightBuffaloQty; }
    public BigDecimal getDailyTotalLitres() { return dailyTotalLitres; }
    public void setDailyTotalLitres(BigDecimal dailyTotalLitres) { this.dailyTotalLitres = dailyTotalLitres; }
    public BigDecimal getCowRate() { return cowRate; }
    public void setCowRate(BigDecimal cowRate) { this.cowRate = cowRate; }
    public BigDecimal getBuffaloRate() { return buffaloRate; }
    public void setBuffaloRate(BigDecimal buffaloRate) { this.buffaloRate = buffaloRate; }
    public BigDecimal getDailyAmount() { return dailyAmount; }
    public void setDailyAmount(BigDecimal dailyAmount) { this.dailyAmount = dailyAmount; }
}
