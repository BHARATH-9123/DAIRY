package com.mdairy.controller;

import com.mdairy.dto.BillGenerationRequest;
import com.mdairy.dto.VendorBillDto;
import com.mdairy.service.VendorBillingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-bills")
public class VendorBillingController {

    private final VendorBillingService billingService;

    public VendorBillingController(VendorBillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public ResponseEntity<List<VendorBillDto>> filterBills(
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(billingService.filterBills(vendorId, month, year, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorBillDto> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBillById(id));
    }

    @PostMapping("/generate")
    public ResponseEntity<VendorBillDto> generateOrRecalculateBill(@Valid @RequestBody BillGenerationRequest request) {
        return ResponseEntity.ok(billingService.generateOrRecalculateBill(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VendorBillDto> updateBillStatus(
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
