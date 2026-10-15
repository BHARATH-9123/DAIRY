package com.mdairy.controller;

import com.mdairy.dto.CustomerDto;
import com.mdairy.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(customerService.getAllCustomers(search, active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        CustomerDto created = customerService.createCustomer(customerDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDto customerDto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CustomerDto> deactivateCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.deactivateCustomer(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CustomerDto> activateCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.activateCustomer(id));
    }
}
