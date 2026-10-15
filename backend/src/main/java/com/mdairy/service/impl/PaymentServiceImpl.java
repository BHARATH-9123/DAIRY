package com.mdairy.service.impl;

import com.mdairy.dto.PaymentDto;
import com.mdairy.entity.*;
import com.mdairy.exception.BadRequestException;
import com.mdairy.exception.ResourceNotFoundException;
import com.mdairy.repository.*;
import com.mdairy.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final CustomerPaymentRepository customerPaymentRepository;
    private final VendorPaymentRepository vendorPaymentRepository;
    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private final CustomerBillRepository customerBillRepository;
    private final VendorBillRepository vendorBillRepository;

    public PaymentServiceImpl(CustomerPaymentRepository customerPaymentRepository,
                              VendorPaymentRepository vendorPaymentRepository,
                              CustomerRepository customerRepository,
                              VendorRepository vendorRepository,
                              CustomerBillRepository customerBillRepository,
                              VendorBillRepository vendorBillRepository) {
        this.customerPaymentRepository = customerPaymentRepository;
        this.vendorPaymentRepository = vendorPaymentRepository;
        this.customerRepository = customerRepository;
        this.vendorRepository = vendorRepository;
        this.customerBillRepository = customerBillRepository;
        this.vendorBillRepository = vendorBillRepository;
    }

    @Override
    public PaymentDto recordCustomerPayment(PaymentDto dto) {
        if (dto.getPartyId() == null) {
            throw new BadRequestException("Customer ID is required");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }
        if (dto.getPaymentDate() == null) {
            dto.setPaymentDate(LocalDate.now());
        }

        Customer customer = customerRepository.findById(dto.getPartyId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getPartyId()));

        CustomerBill bill = null;
        if (dto.getBillId() != null) {
            bill = customerBillRepository.findById(dto.getBillId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer bill not found with id: " + dto.getBillId()));

            if (!bill.getCustomer().getId().equals(customer.getId())) {
                throw new BadRequestException("Bill does not belong to the specified customer");
            }
        }

        CustomerPayment payment = new CustomerPayment();
        payment.setCustomer(customer);
        payment.setBill(bill);
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setAmount(dto.getAmount().setScale(2, RoundingMode.HALF_UP));
        payment.setPaymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod().toUpperCase() : "CASH");
        payment.setReferenceNumber(dto.getReferenceNumber());
        payment.setNotes(dto.getNotes());

        String seq = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String payNumber = String.format("PAY-C-%s-%s",
                dto.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")), seq);
        payment.setPaymentNumber(payNumber);

        CustomerPayment saved = customerPaymentRepository.save(payment);

        // Update bill paid and balance amounts if linked
        if (bill != null) {
            BigDecimal totalPaid = customerPaymentRepository.sumPaymentsForBill(bill.getId());
            if (totalPaid == null) totalPaid = BigDecimal.ZERO;
            bill.setPaidAmount(totalPaid.setScale(2, RoundingMode.HALF_UP));
            BigDecimal newBalance = bill.getTotalAmount().subtract(totalPaid).setScale(2, RoundingMode.HALF_UP);
            bill.setBalanceAmount(newBalance);

            if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
                bill.setStatus("PAID");
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                bill.setStatus("PARTIALLY_PAID");
            }
            customerBillRepository.save(bill);
        }

        return mapCustomerPaymentToDto(saved);
    }

    @Override
    public PaymentDto recordVendorPayment(PaymentDto dto) {
        if (dto.getPartyId() == null) {
            throw new BadRequestException("Vendor ID is required");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }
        if (dto.getPaymentDate() == null) {
            dto.setPaymentDate(LocalDate.now());
        }

        Vendor vendor = vendorRepository.findById(dto.getPartyId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + dto.getPartyId()));

        VendorBill bill = null;
        if (dto.getBillId() != null) {
            bill = vendorBillRepository.findById(dto.getBillId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id: " + dto.getBillId()));

            if (!bill.getVendor().getId().equals(vendor.getId())) {
                throw new BadRequestException("Bill does not belong to the specified vendor");
            }
        }

        VendorPayment payment = new VendorPayment();
        payment.setVendor(vendor);
        payment.setBill(bill);
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setAmount(dto.getAmount().setScale(2, RoundingMode.HALF_UP));
        payment.setPaymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod().toUpperCase() : "CASH");
        payment.setReferenceNumber(dto.getReferenceNumber());
        payment.setNotes(dto.getNotes());

        String seq = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String payNumber = String.format("PAY-V-%s-%s",
                dto.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")), seq);
        payment.setPaymentNumber(payNumber);

        VendorPayment saved = vendorPaymentRepository.save(payment);

        if (bill != null) {
            BigDecimal totalPaid = vendorPaymentRepository.sumPaymentsForBill(bill.getId());
            if (totalPaid == null) totalPaid = BigDecimal.ZERO;
            bill.setPaidAmount(totalPaid.setScale(2, RoundingMode.HALF_UP));
            BigDecimal newBalance = bill.getTotalAmount().subtract(totalPaid).setScale(2, RoundingMode.HALF_UP);
            bill.setBalanceAmount(newBalance);

            if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
                bill.setStatus("PAID");
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                bill.setStatus("PARTIALLY_PAID");
            }
            vendorBillRepository.save(bill);
        }

        return mapVendorPaymentToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> filterCustomerPayments(Long customerId, LocalDate startDate, LocalDate endDate) {
        return customerPaymentRepository.filterPayments(customerId, startDate, endDate).stream()
                .map(this::mapCustomerPaymentToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> filterVendorPayments(Long vendorId, LocalDate startDate, LocalDate endDate) {
        return vendorPaymentRepository.filterPayments(vendorId, startDate, endDate).stream()
                .map(this::mapVendorPaymentToDto)
                .collect(Collectors.toList());
    }

    private PaymentDto mapCustomerPaymentToDto(CustomerPayment p) {
        PaymentDto dto = new PaymentDto();
        dto.setId(p.getId());
        dto.setPaymentNumber(p.getPaymentNumber());
        dto.setPartyId(p.getCustomer().getId());
        dto.setPartyCode(p.getCustomer().getCustomerCode());
        dto.setPartyName(p.getCustomer().getName());
        if (p.getBill() != null) {
            dto.setBillId(p.getBill().getId());
            dto.setBillNumber(p.getBill().getBillNumber());
        }
        dto.setPaymentDate(p.getPaymentDate());
        dto.setAmount(p.getAmount());
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setReferenceNumber(p.getReferenceNumber());
        dto.setNotes(p.getNotes());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    private PaymentDto mapVendorPaymentToDto(VendorPayment p) {
        PaymentDto dto = new PaymentDto();
        dto.setId(p.getId());
        dto.setPaymentNumber(p.getPaymentNumber());
        dto.setPartyId(p.getVendor().getId());
        dto.setPartyCode(p.getVendor().getVendorCode());
        dto.setPartyName(p.getVendor().getName());
        if (p.getBill() != null) {
            dto.setBillId(p.getBill().getId());
            dto.setBillNumber(p.getBill().getBillNumber());
        }
        dto.setPaymentDate(p.getPaymentDate());
        dto.setAmount(p.getAmount());
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setReferenceNumber(p.getReferenceNumber());
        dto.setNotes(p.getNotes());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}
