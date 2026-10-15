package com.mdairy.controller;

import com.mdairy.dto.PaymentDto;
import com.mdairy.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/customer-payments")
public class CustomerPaymentController {

    private final PaymentService paymentService;

    public CustomerPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> filterCustomerPayments(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(paymentService.filterCustomerPayments(customerId, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<PaymentDto> recordCustomerPayment(@Valid @RequestBody PaymentDto paymentDto) {
        PaymentDto created = paymentService.recordCustomerPayment(paymentDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
