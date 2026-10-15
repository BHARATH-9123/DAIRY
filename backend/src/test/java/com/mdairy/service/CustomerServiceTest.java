package com.mdairy.service;

import com.mdairy.dto.CustomerDto;
import com.mdairy.exception.BadRequestException;
import com.mdairy.exception.DuplicateResourceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Test
    @DisplayName("Test Customer creation and duplicate customer code prevention")
    public void testCustomerCreationAndDuplicateCode() {
        String code = "CUST-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        CustomerDto dto = new CustomerDto();
        dto.setCustomerCode(code);
        dto.setName("Ramesh Patel");
        dto.setPhone("9876543210");
        dto.setMilkType("COW");
        dto.setDeliverySchedule("MORNING");
        dto.setCowRate(new BigDecimal("60.00"));
        dto.setBuffaloRate(new BigDecimal("80.00"));

        CustomerDto created = customerService.createCustomer(dto);
        assertNotNull(created.getId());
        assertEquals(code, created.getCustomerCode());
        assertTrue(created.getActive());

        // Duplicate code creation should fail
        CustomerDto duplicate = new CustomerDto();
        duplicate.setCustomerCode(code);
        duplicate.setName("Duplicate Patel");
        duplicate.setCowRate(new BigDecimal("60.00"));
        duplicate.setBuffaloRate(new BigDecimal("80.00"));

        assertThrows(DuplicateResourceException.class, () -> {
            customerService.createCustomer(duplicate);
        });
    }

    @Test
    @DisplayName("Test Customer negative rate validation")
    public void testNegativeRateValidation() {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerCode("NEG-" + UUID.randomUUID().toString().substring(0, 5));
        dto.setName("Invalid Rate");
        dto.setCowRate(new BigDecimal("-10.00"));
        dto.setBuffaloRate(new BigDecimal("80.00"));

        assertThrows(BadRequestException.class, () -> {
            customerService.createCustomer(dto);
        });
    }
}
