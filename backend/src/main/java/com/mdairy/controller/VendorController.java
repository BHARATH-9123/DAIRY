package com.mdairy.controller;

import com.mdairy.dto.VendorDto;
import com.mdairy.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public ResponseEntity<List<VendorDto>> getAllVendors(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(vendorService.getAllVendors(search, active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorDto> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    @PostMapping
    public ResponseEntity<VendorDto> createVendor(@Valid @RequestBody VendorDto vendorDto) {
        VendorDto created = vendorService.createVendor(vendorDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendorDto> updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody VendorDto vendorDto) {
        return ResponseEntity.ok(vendorService.updateVendor(id, vendorDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<VendorDto> deactivateVendor(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.deactivateVendor(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<VendorDto> activateVendor(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.activateVendor(id));
    }
}
