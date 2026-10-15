package com.mdairy.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyAttendanceSummaryDto {

    private LocalDate date;
    private long totalPresent;
    private long totalAbsent;
    private BigDecimal morningCowQty = BigDecimal.ZERO;
    private BigDecimal morningBuffaloQty = BigDecimal.ZERO;
    private BigDecimal nightCowQty = BigDecimal.ZERO;
    private BigDecimal nightBuffaloQty = BigDecimal.ZERO;
    private BigDecimal totalCowQty = BigDecimal.ZERO;
    private BigDecimal totalBuffaloQty = BigDecimal.ZERO;
    private BigDecimal totalLitres = BigDecimal.ZERO;

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public long getTotalPresent() { return totalPresent; }
    public void setTotalPresent(long totalPresent) { this.totalPresent = totalPresent; }
    public long getTotalAbsent() { return totalAbsent; }
    public void setTotalAbsent(long totalAbsent) { this.totalAbsent = totalAbsent; }
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
}
