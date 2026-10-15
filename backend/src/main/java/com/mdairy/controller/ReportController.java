package com.mdairy.controller;

import com.mdairy.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily-customer-attendance")
    public ResponseEntity<List<Map<String, Object>>> getDailyCustomerAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getDailyCustomerAttendanceReport(date));
    }

    @GetMapping("/monthly-customer-attendance")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyCustomerAttendance(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(reportService.getMonthlyCustomerAttendanceReport(customerId, month, year));
    }

    @GetMapping("/daily-vendor-attendance")
    public ResponseEntity<List<Map<String, Object>>> getDailyVendorAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getDailyVendorAttendanceReport(date));
    }

    @GetMapping("/monthly-vendor-attendance")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyVendorAttendance(
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(reportService.getMonthlyVendorAttendanceReport(vendorId, month, year));
    }

    @GetMapping("/customer-milk-delivery")
    public ResponseEntity<List<Map<String, Object>>> getCustomerMilkDelivery(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getCustomerMilkDeliveryReport(startDate, endDate));
    }

    @GetMapping("/vendor-milk-collection")
    public ResponseEntity<List<Map<String, Object>>> getVendorMilkCollection(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getVendorMilkCollectionReport(startDate, endDate));
    }

    @GetMapping("/customer-billing")
    public ResponseEntity<List<Map<String, Object>>> getCustomerBilling(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(reportService.getCustomerBillingReport(month, year));
    }

    @GetMapping("/vendor-billing")
    public ResponseEntity<List<Map<String, Object>>> getVendorBilling(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(reportService.getVendorBillingReport(month, year));
    }

    @GetMapping("/customer-payments")
    public ResponseEntity<List<Map<String, Object>>> getCustomerPayments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getCustomerPaymentsReport(startDate, endDate));
    }

    @GetMapping("/vendor-payments")
    public ResponseEntity<List<Map<String, Object>>> getVendorPayments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getVendorPaymentsReport(startDate, endDate));
    }

    @GetMapping("/outstanding-customer-balances")
    public ResponseEntity<List<Map<String, Object>>> getOutstandingCustomerBalances() {
        return ResponseEntity.ok(reportService.getOutstandingCustomerBalancesReport());
    }

    @GetMapping("/outstanding-vendor-balances")
    public ResponseEntity<List<Map<String, Object>>> getOutstandingVendorBalances() {
        return ResponseEntity.ok(reportService.getOutstandingVendorBalancesReport());
    }
}
