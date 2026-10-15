package com.mdairy.service;

import com.mdairy.dto.PaymentDto;
import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    PaymentDto recordCustomerPayment(PaymentDto paymentDto);
    PaymentDto recordVendorPayment(PaymentDto paymentDto);
    List<PaymentDto> filterCustomerPayments(Long customerId, LocalDate startDate, LocalDate endDate);
    List<PaymentDto> filterVendorPayments(Long vendorId, LocalDate startDate, LocalDate endDate);
}
