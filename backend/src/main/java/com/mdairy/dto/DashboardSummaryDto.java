package com.mdairy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DashboardSummaryDto {

    private long totalActiveCustomers;
    private long totalActiveVendors;
    private BigDecimal todayCustomerMilkDelivery = BigDecimal.ZERO;
    private BigDecimal todayVendorMilkCollection = BigDecimal.ZERO;
    private BigDecimal currentMonthCustomerBilling = BigDecimal.ZERO;
    private BigDecimal currentMonthVendorBilling = BigDecimal.ZERO;
    private BigDecimal outstandingCustomerPayments = BigDecimal.ZERO;
    private BigDecimal outstandingVendorPayments = BigDecimal.ZERO;

    private List<ActivityItemDto> recentActivity = new ArrayList<>();

    public static class ActivityItemDto {
        private String id;
        private String title;
        private String description;
        private String type; // CUSTOMER, VENDOR, ATTENDANCE, BILL, PAYMENT
        private LocalDateTime timestamp;

        public ActivityItemDto() {}

        public ActivityItemDto(String id, String title, String description, String type, LocalDateTime timestamp) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.type = type;
            this.timestamp = timestamp;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    // Getters and Setters
    public long getTotalActiveCustomers() { return totalActiveCustomers; }
    public void setTotalActiveCustomers(long totalActiveCustomers) { this.totalActiveCustomers = totalActiveCustomers; }
    public long getTotalActiveVendors() { return totalActiveVendors; }
    public void setTotalActiveVendors(long totalActiveVendors) { this.totalActiveVendors = totalActiveVendors; }
    public BigDecimal getTodayCustomerMilkDelivery() { return todayCustomerMilkDelivery; }
    public void setTodayCustomerMilkDelivery(BigDecimal todayCustomerMilkDelivery) { this.todayCustomerMilkDelivery = todayCustomerMilkDelivery; }
    public BigDecimal getTodayVendorMilkCollection() { return todayVendorMilkCollection; }
    public void setTodayVendorMilkCollection(BigDecimal todayVendorMilkCollection) { this.todayVendorMilkCollection = todayVendorMilkCollection; }
    public BigDecimal getCurrentMonthCustomerBilling() { return currentMonthCustomerBilling; }
    public void setCurrentMonthCustomerBilling(BigDecimal currentMonthCustomerBilling) { this.currentMonthCustomerBilling = currentMonthCustomerBilling; }
    public BigDecimal getCurrentMonthVendorBilling() { return currentMonthVendorBilling; }
    public void setCurrentMonthVendorBilling(BigDecimal currentMonthVendorBilling) { this.currentMonthVendorBilling = currentMonthVendorBilling; }
    public BigDecimal getOutstandingCustomerPayments() { return outstandingCustomerPayments; }
    public void setOutstandingCustomerPayments(BigDecimal outstandingCustomerPayments) { this.outstandingCustomerPayments = outstandingCustomerPayments; }
    public BigDecimal getOutstandingVendorPayments() { return outstandingVendorPayments; }
    public void setOutstandingVendorPayments(BigDecimal outstandingVendorPayments) { this.outstandingVendorPayments = outstandingVendorPayments; }
    public List<ActivityItemDto> getRecentActivity() { return recentActivity; }
    public void setRecentActivity(List<ActivityItemDto> recentActivity) { this.recentActivity = recentActivity; }
}
