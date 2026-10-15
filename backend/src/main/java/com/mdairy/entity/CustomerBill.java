package com.mdairy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_bills", uniqueConstraints = {
    @UniqueConstraint(name = "uk_customer_bill_period", columnNames = {"customer_id", "billing_month", "billing_year"}),
    @UniqueConstraint(name = "uk_customer_bill_number", columnNames = {"bill_number"})
})
public class CustomerBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", nullable = false, length = 50)
    private String billNumber;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "billing_month", nullable = false)
    private Integer billingMonth;

    @Column(name = "billing_year", nullable = false)
    private Integer billingYear;

    @Column(name = "cow_litres", nullable = false, precision = 10, scale = 2)
    private BigDecimal cowLitres = BigDecimal.ZERO;

    @Column(name = "buffalo_litres", nullable = false, precision = 10, scale = 2)
    private BigDecimal buffaloLitres = BigDecimal.ZERO;

    @Column(name = "total_litres", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLitres = BigDecimal.ZERO;

    @Column(name = "cow_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal cowRate = BigDecimal.ZERO;

    @Column(name = "buffalo_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal buffaloRate = BigDecimal.ZERO;

    @Column(name = "cow_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal cowAmount = BigDecimal.ZERO;

    @Column(name = "buffalo_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal buffaloAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "balance_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "DRAFT"; // DRAFT, ISSUED, PAID, PARTIALLY_PAID

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CustomerBillItem> items = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
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

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(Integer billingMonth) {
        this.billingMonth = billingMonth;
    }

    public Integer getBillingYear() {
        return billingYear;
    }

    public void setBillingYear(Integer billingYear) {
        this.billingYear = billingYear;
    }

    public BigDecimal getCowLitres() {
        return cowLitres;
    }

    public void setCowLitres(BigDecimal cowLitres) {
        this.cowLitres = cowLitres;
    }

    public BigDecimal getBuffaloLitres() {
        return buffaloLitres;
    }

    public void setBuffaloLitres(BigDecimal buffaloLitres) {
        this.buffaloLitres = buffaloLitres;
    }

    public BigDecimal getTotalLitres() {
        return totalLitres;
    }

    public void setTotalLitres(BigDecimal totalLitres) {
        this.totalLitres = totalLitres;
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

    public BigDecimal getCowAmount() {
        return cowAmount;
    }

    public void setCowAmount(BigDecimal cowAmount) {
        this.cowAmount = cowAmount;
    }

    public BigDecimal getBuffaloAmount() {
        return buffaloAmount;
    }

    public void setBuffaloAmount(BigDecimal buffaloAmount) {
        this.buffaloAmount = buffaloAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<CustomerBillItem> getItems() {
        return items;
    }

    public void setItems(List<CustomerBillItem> items) {
        this.items = items;
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
