package com.mdairy.service.impl;

import com.mdairy.entity.*;
import com.mdairy.repository.*;
import com.mdairy.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final CustomerAttendanceRepository customerAttendanceRepository;
    private final VendorAttendanceRepository vendorAttendanceRepository;
    private final CustomerBillRepository customerBillRepository;
    private final VendorBillRepository vendorBillRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final VendorPaymentRepository vendorPaymentRepository;
    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;

    public ReportServiceImpl(CustomerAttendanceRepository customerAttendanceRepository,
                             VendorAttendanceRepository vendorAttendanceRepository,
                             CustomerBillRepository customerBillRepository,
                             VendorBillRepository vendorBillRepository,
                             CustomerPaymentRepository customerPaymentRepository,
                             VendorPaymentRepository vendorPaymentRepository,
                             CustomerRepository customerRepository,
                             VendorRepository vendorRepository) {
        this.customerAttendanceRepository = customerAttendanceRepository;
        this.vendorAttendanceRepository = vendorAttendanceRepository;
        this.customerBillRepository = customerBillRepository;
        this.vendorBillRepository = vendorBillRepository;
        this.customerPaymentRepository = customerPaymentRepository;
        this.vendorPaymentRepository = vendorPaymentRepository;
        this.customerRepository = customerRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public List<Map<String, Object>> getDailyCustomerAttendanceReport(LocalDate date) {
        if (date == null) date = LocalDate.now();
        List<CustomerAttendance> records = customerAttendanceRepository.findByAttendanceDateOrderByCustomerCustomerCodeAsc(date);
        List<Map<String, Object>> report = new ArrayList<>();
        for (CustomerAttendance ca : records) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", ca.getAttendanceDate().toString());
            map.put("customerCode", ca.getCustomer().getCustomerCode());
            map.put("customerName", ca.getCustomer().getName());
            map.put("status", ca.getStatus());
            map.put("morningCowQty", ca.getMorningCowQty());
            map.put("morningBuffaloQty", ca.getMorningBuffaloQty());
            map.put("nightCowQty", ca.getNightCowQty());
            map.put("nightBuffaloQty", ca.getNightBuffaloQty());
            map.put("totalLitres", ca.getTotalLitres());
            report.add(map);
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getMonthlyCustomerAttendanceReport(Long customerId, Integer month, Integer year) {
        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<CustomerAttendance> records;
        if (customerId != null) {
            records = customerAttendanceRepository.findByCustomerIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(customerId, start, end);
        } else {
            records = customerAttendanceRepository.findByAttendanceDateBetweenOrderByAttendanceDateAsc(start, end);
        }

        List<Map<String, Object>> report = new ArrayList<>();
        for (CustomerAttendance ca : records) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", ca.getAttendanceDate().toString());
            map.put("customerCode", ca.getCustomer().getCustomerCode());
            map.put("customerName", ca.getCustomer().getName());
            map.put("status", ca.getStatus());
            map.put("cowLitres", ca.getTotalCowQty());
            map.put("buffaloLitres", ca.getTotalBuffaloQty());
            map.put("totalLitres", ca.getTotalLitres());
            report.add(map);
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getDailyVendorAttendanceReport(LocalDate date) {
        if (date == null) date = LocalDate.now();
        List<VendorAttendance> records = vendorAttendanceRepository.findByAttendanceDateOrderByVendorVendorCodeAsc(date);
        List<Map<String, Object>> report = new ArrayList<>();
        for (VendorAttendance va : records) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", va.getAttendanceDate().toString());
            map.put("vendorCode", va.getVendor().getVendorCode());
            map.put("vendorName", va.getVendor().getName());
            map.put("status", va.getStatus());
            map.put("morningCowQty", va.getMorningCowQty());
            map.put("morningBuffaloQty", va.getMorningBuffaloQty());
            map.put("nightCowQty", va.getNightCowQty());
            map.put("nightBuffaloQty", va.getNightBuffaloQty());
            map.put("totalLitres", va.getTotalLitres());
            report.add(map);
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getMonthlyVendorAttendanceReport(Long vendorId, Integer month, Integer year) {
        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<VendorAttendance> records;
        if (vendorId != null) {
            records = vendorAttendanceRepository.findByVendorIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(vendorId, start, end);
        } else {
            records = vendorAttendanceRepository.findByAttendanceDateBetweenOrderByAttendanceDateAsc(start, end);
        }

        List<Map<String, Object>> report = new ArrayList<>();
        for (VendorAttendance va : records) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", va.getAttendanceDate().toString());
            map.put("vendorCode", va.getVendor().getVendorCode());
            map.put("vendorName", va.getVendor().getName());
            map.put("status", va.getStatus());
            map.put("cowLitres", va.getTotalCowQty());
            map.put("buffaloLitres", va.getTotalBuffaloQty());
            map.put("totalLitres", va.getTotalLitres());
            report.add(map);
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getCustomerMilkDeliveryReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        List<CustomerAttendance> records = customerAttendanceRepository.findByAttendanceDateBetweenOrderByAttendanceDateAsc(startDate, endDate);
        List<Map<String, Object>> report = new ArrayList<>();
        for (CustomerAttendance ca : records) {
            if ("PRESENT".equalsIgnoreCase(ca.getStatus())) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("date", ca.getAttendanceDate().toString());
                map.put("customerCode", ca.getCustomer().getCustomerCode());
                map.put("customerName", ca.getCustomer().getName());
                map.put("cowLitres", ca.getTotalCowQty());
                map.put("buffaloLitres", ca.getTotalBuffaloQty());
                map.put("totalLitres", ca.getTotalLitres());
                report.add(map);
            }
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getVendorMilkCollectionReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        List<VendorAttendance> records = vendorAttendanceRepository.findByAttendanceDateBetweenOrderByAttendanceDateAsc(startDate, endDate);
        List<Map<String, Object>> report = new ArrayList<>();
        for (VendorAttendance va : records) {
            if ("PRESENT".equalsIgnoreCase(va.getStatus())) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("date", va.getAttendanceDate().toString());
                map.put("vendorCode", va.getVendor().getVendorCode());
                map.put("vendorName", va.getVendor().getName());
                map.put("cowLitres", va.getTotalCowQty());
                map.put("buffaloLitres", va.getTotalBuffaloQty());
                map.put("totalLitres", va.getTotalLitres());
                report.add(map);
            }
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getCustomerBillingReport(Integer month, Integer year) {
        List<CustomerBill> bills = customerBillRepository.filterBills(null, month, year, null);
        List<Map<String, Object>> report = new ArrayList<>();
        for (CustomerBill b : bills) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("billNumber", b.getBillNumber());
            map.put("customerCode", b.getCustomer().getCustomerCode());
            map.put("customerName", b.getCustomer().getName());
            map.put("monthYear", String.format("%02d/%d", b.getBillingMonth(), b.getBillingYear()));
            map.put("cowLitres", b.getCowLitres());
            map.put("buffaloLitres", b.getBuffaloLitres());
            map.put("totalLitres", b.getTotalLitres());
            map.put("cowAmount", b.getCowAmount());
            map.put("buffaloAmount", b.getBuffaloAmount());
            map.put("totalAmount", b.getTotalAmount());
            map.put("paidAmount", b.getPaidAmount());
            map.put("balanceAmount", b.getBalanceAmount());
            map.put("status", b.getStatus());
            report.add(map);
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getVendorBillingReport(Integer month, Integer year) {
        List<VendorBill> bills = vendorBillRepository.filterBills(null, month, year, null);
        List<Map<String, Object>> report = new ArrayList<>();
        for (VendorBill b : bills) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("billNumber", b.getBillNumber());
            map.put("vendorCode", b.getVendor().getVendorCode());
            map.put("vendorName", b.getVendor().getName());
            map.put("monthYear", String.format("%02d/%d", b.getBillingMonth(), b.getBillingYear()));
            map.put("cowLitres", b.getCowLitres());
            map.put("buffaloLitres", b.getBuffaloLitres());
            map.put("totalLitres", b.getTotalLitres());
            map.put("cowAmount", b.getCowAmount());
            map.put("buffaloAmount", b.getBuffaloAmount());
            map.put("totalAmount", b.getTotalAmount());
            map.put("paidAmount", b.getPaidAmount());
            map.put("balanceAmount", b.getBalanceAmount());
            map.put("status", b.getStatus());
            report.add(map);
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getCustomerPaymentsReport(LocalDate startDate, LocalDate endDate) {
        List<CustomerPayment> payments = customerPaymentRepository.filterPayments(null, startDate, endDate);
        List<Map<String, Object>> report = new ArrayList<>();
        for (CustomerPayment p : payments) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("paymentNumber", p.getPaymentNumber());
            map.put("date", p.getPaymentDate().toString());
            map.put("customerCode", p.getCustomer().getCustomerCode());
            map.put("customerName", p.getCustomer().getName());
            map.put("billNumber", p.getBill() != null ? p.getBill().getBillNumber() : "-");
            map.put("amount", p.getAmount());
            map.put("method", p.getPaymentMethod());
            map.put("reference", p.getReferenceNumber() != null ? p.getReferenceNumber() : "-");
            report.add(map);
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getVendorPaymentsReport(LocalDate startDate, LocalDate endDate) {
        List<VendorPayment> payments = vendorPaymentRepository.filterPayments(null, startDate, endDate);
        List<Map<String, Object>> report = new ArrayList<>();
        for (VendorPayment p : payments) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("paymentNumber", p.getPaymentNumber());
            map.put("date", p.getPaymentDate().toString());
            map.put("vendorCode", p.getVendor().getVendorCode());
            map.put("vendorName", p.getVendor().getName());
            map.put("billNumber", p.getBill() != null ? p.getBill().getBillNumber() : "-");
            map.put("amount", p.getAmount());
            map.put("method", p.getPaymentMethod());
            map.put("reference", p.getReferenceNumber() != null ? p.getReferenceNumber() : "-");
            report.add(map);
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getOutstandingCustomerBalancesReport() {
        List<CustomerBill> bills = customerBillRepository.filterBills(null, null, null, null);
        List<Map<String, Object>> report = new ArrayList<>();
        for (CustomerBill b : bills) {
            if (b.getBalanceAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("customerCode", b.getCustomer().getCustomerCode());
                map.put("customerName", b.getCustomer().getName());
                map.put("phone", b.getCustomer().getPhone());
                map.put("billNumber", b.getBillNumber());
                map.put("monthYear", String.format("%02d/%d", b.getBillingMonth(), b.getBillingYear()));
                map.put("totalAmount", b.getTotalAmount());
                map.put("paidAmount", b.getPaidAmount());
                map.put("balanceAmount", b.getBalanceAmount());
                map.put("status", b.getStatus());
                report.add(map);
            }
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getOutstandingVendorBalancesReport() {
        List<VendorBill> bills = vendorBillRepository.filterBills(null, null, null, null);
        List<Map<String, Object>> report = new ArrayList<>();
        for (VendorBill b : bills) {
            if (b.getBalanceAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("vendorCode", b.getVendor().getVendorCode());
                map.put("vendorName", b.getVendor().getName());
                map.put("phone", b.getVendor().getPhone());
                map.put("billNumber", b.getBillNumber());
                map.put("monthYear", String.format("%02d/%d", b.getBillingMonth(), b.getBillingYear()));
                map.put("totalAmount", b.getTotalAmount());
                map.put("paidAmount", b.getPaidAmount());
                map.put("balanceAmount", b.getBalanceAmount());
                map.put("status", b.getStatus());
                report.add(map);
            }
        }
        return report;
    }
}
