package com.mdairy.service.impl;

import com.mdairy.dto.CustomerDto;
import com.mdairy.entity.Customer;
import com.mdairy.exception.BadRequestException;
import com.mdairy.exception.DuplicateResourceException;
import com.mdairy.exception.ResourceNotFoundException;
import com.mdairy.repository.CustomerRepository;
import com.mdairy.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers(String search, Boolean active) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return customerRepository.searchCustomers(query, active).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToDto(customer);
    }

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        if (customerDto.getCustomerCode() == null || customerDto.getCustomerCode().trim().isEmpty()) {
            throw new BadRequestException("Customer code cannot be empty");
        }
        String code = customerDto.getCustomerCode().trim();
        if (customerRepository.existsByCustomerCode(code)) {
            throw new DuplicateResourceException("Customer code '" + code + "' already exists");
        }

        validateRatesAndQuantities(customerDto);

        Customer customer = new Customer();
        mapToEntity(customerDto, customer);
        customer.setCustomerCode(code);
        Customer saved = customerRepository.save(customer);
        return mapToDto(saved);
    }

    @Override
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        String code = customerDto.getCustomerCode().trim();
        if (customerRepository.existsByCustomerCodeAndIdNot(code, id)) {
            throw new DuplicateResourceException("Customer code '" + code + "' is already in use by another customer");
        }

        validateRatesAndQuantities(customerDto);

        mapToEntity(customerDto, customer);
        customer.setCustomerCode(code);
        Customer updated = customerRepository.save(customer);
        return mapToDto(updated);
    }

    @Override
    public CustomerDto deactivateCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setActive(false);
        return mapToDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDto activateCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setActive(true);
        return mapToDto(customerRepository.save(customer));
    }

    private void validateRatesAndQuantities(CustomerDto dto) {
        if (dto.getCowRate() != null && dto.getCowRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Cow rate cannot be negative");
        }
        if (dto.getBuffaloRate() != null && dto.getBuffaloRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Buffalo rate cannot be negative");
        }
        if (dto.getMorningCowQty() != null && dto.getMorningCowQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Morning cow quantity cannot be negative");
        }
        if (dto.getMorningBuffaloQty() != null && dto.getMorningBuffaloQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Morning buffalo quantity cannot be negative");
        }
        if (dto.getNightCowQty() != null && dto.getNightCowQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Night cow quantity cannot be negative");
        }
        if (dto.getNightBuffaloQty() != null && dto.getNightBuffaloQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Night buffalo quantity cannot be negative");
        }
    }

    private CustomerDto mapToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setMilkType(customer.getMilkType());
        dto.setDeliverySchedule(customer.getDeliverySchedule());
        dto.setMorningCowQty(customer.getMorningCowQty());
        dto.setMorningBuffaloQty(customer.getMorningBuffaloQty());
        dto.setNightCowQty(customer.getNightCowQty());
        dto.setNightBuffaloQty(customer.getNightBuffaloQty());
        dto.setCowRate(customer.getCowRate());
        dto.setBuffaloRate(customer.getBuffaloRate());
        dto.setNotes(customer.getNotes());
        dto.setActive(customer.getActive());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        return dto;
    }

    private void mapToEntity(CustomerDto dto, Customer entity) {
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setMilkType(dto.getMilkType() != null ? dto.getMilkType() : "COW");
        entity.setDeliverySchedule(dto.getDeliverySchedule() != null ? dto.getDeliverySchedule() : "MORNING");
        entity.setMorningCowQty(dto.getMorningCowQty() != null ? dto.getMorningCowQty() : BigDecimal.ZERO);
        entity.setMorningBuffaloQty(dto.getMorningBuffaloQty() != null ? dto.getMorningBuffaloQty() : BigDecimal.ZERO);
        entity.setNightCowQty(dto.getNightCowQty() != null ? dto.getNightCowQty() : BigDecimal.ZERO);
        entity.setNightBuffaloQty(dto.getNightBuffaloQty() != null ? dto.getNightBuffaloQty() : BigDecimal.ZERO);
        entity.setCowRate(dto.getCowRate() != null ? dto.getCowRate() : BigDecimal.valueOf(60.00));
        entity.setBuffaloRate(dto.getBuffaloRate() != null ? dto.getBuffaloRate() : BigDecimal.valueOf(80.00));
        entity.setNotes(dto.getNotes());
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
