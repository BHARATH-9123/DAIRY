package com.mdairy.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CustomerBillDto {

    private Long id;
    private String billNumber;
    private Long customerId;
    private String customerCode;
    private String customerName;
    private String customerPhone;
    private String customerAddress;

    private Integer billingMonth;
    private Integer billingYear;

    private BigDecimal cowLitres;
    private BigDecimal buffaloLitres;
    private BigDecimal totalLitres;

    private BigDecimal cowRate;
    private BigDecimal buffaloRate;

    private BigDecimal cowAmount;
    private BigDecimal buffaloAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;

    private String status;
    private String notes;
    private List<CustomerBillItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public Integer getBillingMonth() { return billingMonth; }
    public void setBillingMonth(Integer billingMonth) { this.billingMonth = billingMonth; }
    public Integer getBillingYear() { return billingYear; }
    public void setBillingYear(Integer billingYear) { this.billingYear = billingYear; }
    public BigDecimal getCowLitres() { return cowLitres; }
    public void setCowLitres(BigDecimal cowLitres) { this.cowLitres = cowLitres; }
    public BigDecimal getBuffaloLitres() { return buffaloLitres; }
    public void setBuffaloLitres(BigDecimal buffaloLitres) { this.buffaloLitres = buffaloLitres; }
    public BigDecimal getTotalLitres() { return totalLitres; }
    public void setTotalLitres(BigDecimal totalLitres) { this.totalLitres = totalLitres; }
    public BigDecimal getCowRate() { return cowRate; }
    public void setCowRate(BigDecimal cowRate) { this.cowRate = cowRate; }
    public BigDecimal getBuffaloRate() { return buffaloRate; }
    public void setBuffaloRate(BigDecimal buffaloRate) { this.buffaloRate = buffaloRate; }
    public BigDecimal getCowAmount() { return cowAmount; }
    public void setCowAmount(BigDecimal cowAmount) { this.cowAmount = cowAmount; }
    public BigDecimal getBuffaloAmount() { return buffaloAmount; }
    public void setBuffaloAmount(BigDecimal buffaloAmount) { this.buffaloAmount = buffaloAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<CustomerBillItemDto> getItems() { return items; }
    public void setItems(List<CustomerBillItemDto> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
