package com.mdairy.service.impl;

import com.mdairy.dto.DashboardSummaryDto;
import com.mdairy.entity.CustomerBill;
import com.mdairy.entity.CustomerPayment;
import com.mdairy.entity.VendorBill;
import com.mdairy.entity.VendorPayment;
import com.mdairy.repository.*;
import com.mdairy.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private final CustomerAttendanceRepository customerAttendanceRepository;
    private final VendorAttendanceRepository vendorAttendanceRepository;
    private final CustomerBillRepository customerBillRepository;
    private final VendorBillRepository vendorBillRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final VendorPaymentRepository vendorPaymentRepository;

    public DashboardServiceImpl(CustomerRepository customerRepository,
                                VendorRepository vendorRepository,
                                CustomerAttendanceRepository customerAttendanceRepository,
                                VendorAttendanceRepository vendorAttendanceRepository,
                                CustomerBillRepository customerBillRepository,
                                VendorBillRepository vendorBillRepository,
                                CustomerPaymentRepository customerPaymentRepository,
                                VendorPaymentRepository vendorPaymentRepository) {
        this.customerRepository = customerRepository;
        this.vendorRepository = vendorRepository;
        this.customerAttendanceRepository = customerAttendanceRepository;
        this.vendorAttendanceRepository = vendorAttendanceRepository;
        this.customerBillRepository = customerBillRepository;
        this.vendorBillRepository = vendorBillRepository;
        this.customerPaymentRepository = customerPaymentRepository;
        this.vendorPaymentRepository = vendorPaymentRepository;
    }

    @Override
    public DashboardSummaryDto getDashboardSummary() {
        DashboardSummaryDto summary = new DashboardSummaryDto();

        summary.setTotalActiveCustomers(customerRepository.countByActiveTrue());
        summary.setTotalActiveVendors(vendorRepository.countByActiveTrue());

        LocalDate today = LocalDate.now();
        BigDecimal todayDelivery = customerAttendanceRepository.sumTotalDeliveryForDate(today);
        summary.setTodayCustomerMilkDelivery(todayDelivery != null ? todayDelivery : BigDecimal.ZERO);

        BigDecimal todayCollection = vendorAttendanceRepository.sumTotalCollectionForDate(today);
        summary.setTodayVendorMilkCollection(todayCollection != null ? todayCollection : BigDecimal.ZERO);

        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        BigDecimal custMonthBill = customerBillRepository.sumTotalBilledForMonthAndYear(currentMonth, currentYear);
        summary.setCurrentMonthCustomerBilling(custMonthBill != null ? custMonthBill : BigDecimal.ZERO);

        BigDecimal vendMonthBill = vendorBillRepository.sumTotalBilledForMonthAndYear(currentMonth, currentYear);
        summary.setCurrentMonthVendorBilling(vendMonthBill != null ? vendMonthBill : BigDecimal.ZERO);

        BigDecimal custOutstanding = customerBillRepository.sumTotalOutstandingBalance();
        summary.setOutstandingCustomerPayments(custOutstanding != null ? custOutstanding : BigDecimal.ZERO);

        BigDecimal vendOutstanding = vendorBillRepository.sumTotalOutstandingBalance();
        summary.setOutstandingVendorPayments(vendOutstanding != null ? vendOutstanding : BigDecimal.ZERO);

        // Fetch recent activities
        List<DashboardSummaryDto.ActivityItemDto> activities = new ArrayList<>();

        List<CustomerBill> recentCustBills = customerBillRepository.filterBills(null, null, null, null);
        for (int i = 0; i < Math.min(3, recentCustBills.size()); i++) {
            CustomerBill cb = recentCustBills.get(i);
            activities.add(new DashboardSummaryDto.ActivityItemDto(
                    "CB-" + cb.getId(),
                    "Customer Bill " + cb.getBillNumber(),
                    cb.getCustomer().getName() + " - " + cb.getTotalAmount() + " INR (" + cb.getStatus() + ")",
                    "BILL",
                    cb.getCreatedAt() != null ? cb.getCreatedAt() : LocalDateTime.now()
            ));
        }

        List<CustomerPayment> recentCustPayments = customerPaymentRepository.filterPayments(null, null, null);
        for (int i = 0; i < Math.min(3, recentCustPayments.size()); i++) {
            CustomerPayment cp = recentCustPayments.get(i);
            activities.add(new DashboardSummaryDto.ActivityItemDto(
                    "CP-" + cp.getId(),
                    "Payment Received " + cp.getPaymentNumber(),
                    cp.getCustomer().getName() + " paid " + cp.getAmount() + " INR via " + cp.getPaymentMethod(),
                    "PAYMENT",
                    cp.getCreatedAt() != null ? cp.getCreatedAt() : LocalDateTime.now()
            ));
        }

        activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        if (activities.size() > 6) {
            activities = activities.subList(0, 6);
        }
        summary.setRecentActivity(activities);

        return summary;
    }
}
