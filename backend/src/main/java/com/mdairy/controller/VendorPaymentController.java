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
@RequestMapping("/api/vendor-payments")
public class VendorPaymentController {

    private final PaymentService paymentService;

    public VendorPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> filterVendorPayments(
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(paymentService.filterVendorPayments(vendorId, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<PaymentDto> recordVendorPayment(@Valid @RequestBody PaymentDto paymentDto) {
        PaymentDto created = paymentService.recordVendorPayment(paymentDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
