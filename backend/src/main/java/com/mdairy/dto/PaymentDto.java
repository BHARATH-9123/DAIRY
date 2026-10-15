package com.mdairy.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentDto {

    private Long id;
    private String paymentNumber;

    @NotNull(message = "Party ID (Customer ID or Vendor ID) is required")
    private Long partyId;

    private String partyCode;
    private String partyName;

    private Long billId;
    private String billNumber;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod = "CASH"; // CASH, UPI, BANK_TRANSFER, CHEQUE

    private String referenceNumber;
    private String notes;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }
    public Long getPartyId() { return partyId; }
    public void setPartyId(Long partyId) { this.partyId = partyId; }
    public String getPartyCode() { return partyCode; }
    public void setPartyCode(String partyCode) { this.partyCode = partyCode; }
    public String getPartyName() { return partyName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
