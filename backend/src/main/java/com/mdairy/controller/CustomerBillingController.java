package com.mdairy.controller;

import com.mdairy.dto.BillGenerationRequest;
import com.mdairy.dto.CustomerBillDto;
import com.mdairy.service.CustomerBillingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-bills")
public class CustomerBillingController {

    private final CustomerBillingService billingService;

    public CustomerBillingController(CustomerBillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerBillDto>> filterBills(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(billingService.filterBills(customerId, month, year, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerBillDto> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBillById(id));
    }

    @PostMapping("/generate")
    public ResponseEntity<CustomerBillDto> generateOrRecalculateBill(@Valid @RequestBody BillGenerationRequest request) {
        return ResponseEntity.ok(billingService.generateOrRecalculateBill(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerBillDto> updateBillStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(billingService.updateBillStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraftBill(@PathVariable Long id) {
        billingService.deleteDraftBill(id);
        return ResponseEntity.noContent().build();
    }
}
