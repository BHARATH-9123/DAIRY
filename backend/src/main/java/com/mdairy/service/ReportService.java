package com.mdairy.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Map<String, Object>> getDailyCustomerAttendanceReport(LocalDate date);
    List<Map<String, Object>> getMonthlyCustomerAttendanceReport(Long customerId, Integer month, Integer year);
    List<Map<String, Object>> getDailyVendorAttendanceReport(LocalDate date);
    List<Map<String, Object>> getMonthlyVendorAttendanceReport(Long vendorId, Integer month, Integer year);
    List<Map<String, Object>> getCustomerMilkDeliveryReport(LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getVendorMilkCollectionReport(LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getCustomerBillingReport(Integer month, Integer year);
    List<Map<String, Object>> getVendorBillingReport(Integer month, Integer year);
    List<Map<String, Object>> getCustomerPaymentsReport(LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getVendorPaymentsReport(LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getOutstandingCustomerBalancesReport();
    List<Map<String, Object>> getOutstandingVendorBalancesReport();
}
