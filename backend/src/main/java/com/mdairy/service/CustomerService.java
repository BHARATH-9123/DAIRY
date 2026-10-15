package com.mdairy.service;

import com.mdairy.dto.CustomerDto;
import java.util.List;

public interface CustomerService {
    List<CustomerDto> getAllCustomers(String search, Boolean active);
    CustomerDto getCustomerById(Long id);
    CustomerDto createCustomer(CustomerDto customerDto);
    CustomerDto updateCustomer(Long id, CustomerDto customerDto);
    CustomerDto deactivateCustomer(Long id);
    CustomerDto activateCustomer(Long id);
}
