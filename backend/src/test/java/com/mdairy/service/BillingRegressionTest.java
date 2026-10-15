package com.mdairy.service;

import com.mdairy.dto.BillGenerationRequest;
import com.mdairy.dto.CustomerAttendanceDto;
import com.mdairy.dto.CustomerBillDto;
import com.mdairy.dto.CustomerDto;
import com.mdairy.dto.PaymentDto;
import com.mdairy.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BillingRegressionTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerAttendanceService attendanceService;

    @Autowired
    private CustomerBillingService billingService;

    @Autowired
    private PaymentService paymentService;

    @Test
    @DisplayName("Verify attendance updates and draft billing recalculation regression scenario")
    public void testAttendanceEditAndDraftBillingRecalculationRegression() {
        // Step 1: Create an isolated test customer with Cow rate = ₹100, Buffalo rate = ₹200
        String uniqueCode = "REG-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        CustomerDto customer = new CustomerDto();
        customer.setCustomerCode(uniqueCode);
        customer.setName("Regression Test Customer");
        customer.setMilkType("BOTH");
        customer.setDeliverySchedule("MORNING");
        customer.setCowRate(new BigDecimal("100.00"));
        customer.setBuffaloRate(new BigDecimal("200.00"));
        customer.setMorningCowQty(new BigDecimal("5.00"));
        customer.setMorningBuffaloQty(new BigDecimal("2.00"));

        CustomerDto createdCustomer = customerService.createCustomer(customer);
        Long customerId = createdCustomer.getId();
        assertNotNull(customerId);

        int testMonth = 11;
        int testYear = 2026;

        // Step 2: Record Day 1 attendance: Morning cow = 5, Morning buffalo = 2
        // Expected amount: (5 * 100) + (2 * 200) = 500 + 400 = ₹900
        LocalDate day1 = LocalDate.of(testYear, testMonth, 1);
        CustomerAttendanceDto attDay1 = new CustomerAttendanceDto();
        attDay1.setCustomerId(customerId);
        attDay1.setAttendanceDate(day1);
        attDay1.setStatus("PRESENT");
        attDay1.setMorningCowQty(new BigDecimal("5.00"));
        attDay1.setMorningBuffaloQty(new BigDecimal("2.00"));
        attDay1.setNightCowQty(BigDecimal.ZERO);
        attDay1.setNightBuffaloQty(BigDecimal.ZERO);
        attendanceService.saveOrUpdateAttendance(attDay1);

        // Step 3: Record Day 2 attendance: Morning cow = 3, Morning buffalo = 1
        // Expected amount: (3 * 100) + (1 * 200) = 300 + 200 = ₹500
        LocalDate day2 = LocalDate.of(testYear, testMonth, 2);
        CustomerAttendanceDto attDay2 = new CustomerAttendanceDto();
        attDay2.setCustomerId(customerId);
        attDay2.setAttendanceDate(day2);
        attDay2.setStatus("PRESENT");
        attDay2.setMorningCowQty(new BigDecimal("3.00"));
        attDay2.setMorningBuffaloQty(new BigDecimal("1.00"));
        attDay2.setNightCowQty(BigDecimal.ZERO);
        attDay2.setNightBuffaloQty(BigDecimal.ZERO);
        attendanceService.saveOrUpdateAttendance(attDay2);

        // Step 4: Generate draft bill for testMonth/testYear
        // Expected: Cow = 8 litres, Buffalo = 3 litres, Total = 11 litres, Total Bill = ₹1,400.00
        BillGenerationRequest request = new BillGenerationRequest();
        request.setId(customerId);
        request.setBillingMonth(testMonth);
        request.setBillingYear(testYear);
        request.setRecalculateDraft(false);

        CustomerBillDto initialBill = billingService.generateOrRecalculateBill(request);
        assertNotNull(initialBill);
        assertEquals(0, new BigDecimal("8.00").compareTo(initialBill.getCowLitres()), "Initial cow litres should be 8.00");
        assertEquals(0, new BigDecimal("3.00").compareTo(initialBill.getBuffaloLitres()), "Initial buffalo litres should be 3.00");
        assertEquals(0, new BigDecimal("11.00").compareTo(initialBill.getTotalLitres()), "Initial total litres should be 11.00");
        assertEquals(0, new BigDecimal("1400.00").compareTo(initialBill.getTotalAmount()), "Initial total amount should be ₹1400.00");
        assertEquals("DRAFT", initialBill.getStatus());

        // Step 5: Update Day 2 cow quantity from 3 litres to 4 litres
        CustomerAttendanceDto updatedAttDay2 = new CustomerAttendanceDto();
        updatedAttDay2.setCustomerId(customerId);
        updatedAttDay2.setAttendanceDate(day2);
        updatedAttDay2.setStatus("PRESENT");
        updatedAttDay2.setMorningCowQty(new BigDecimal("4.00")); // Updated from 3 to 4
        updatedAttDay2.setMorningBuffaloQty(new BigDecimal("1.00"));
        updatedAttDay2.setNightCowQty(BigDecimal.ZERO);
        updatedAttDay2.setNightBuffaloQty(BigDecimal.ZERO);
        attendanceService.saveOrUpdateAttendance(updatedAttDay2);

        // Step 6: Recalculate draft bill
        // Expected after update:
        // Cow = 9 litres, Buffalo = 3 litres, Total = 12 litres, Total Bill = ₹1,500.00
        request.setRecalculateDraft(true);
        CustomerBillDto recalculatedBill = billingService.generateOrRecalculateBill(request);

        assertNotNull(recalculatedBill);
        assertEquals(0, new BigDecimal("9.00").compareTo(recalculatedBill.getCowLitres()), "Recalculated cow litres should be 9.00");
        assertEquals(0, new BigDecimal("3.00").compareTo(recalculatedBill.getBuffaloLitres()), "Recalculated buffalo litres should be 3.00");
        assertEquals(0, new BigDecimal("12.00").compareTo(recalculatedBill.getTotalLitres()), "Recalculated total litres should be 12.00");
        assertEquals(0, new BigDecimal("1500.00").compareTo(recalculatedBill.getTotalAmount()), "Recalculated total amount should be ₹1500.00");
        assertEquals(0, new BigDecimal("1500.00").compareTo(recalculatedBill.getBalanceAmount()), "Balance should reflect total without payments");

        // Step 7: Record a valid payment of ₹500 against this bill
        PaymentDto payment = new PaymentDto();
        payment.setPartyId(customerId);
        payment.setBillId(recalculatedBill.getId());
        payment.setAmount(new BigDecimal("500.00"));
        payment.setPaymentDate(day2);
        payment.setPaymentMethod("UPI");
        paymentService.recordCustomerPayment(payment);

        // Fetch refreshed bill: paid should be ₹500, balance ₹1000, status PARTIALLY_PAID
        CustomerBillDto billAfterPayment = billingService.getBillById(recalculatedBill.getId());
        assertEquals(0, new BigDecimal("500.00").compareTo(billAfterPayment.getPaidAmount()), "Paid amount should be 500.00");
        assertEquals(0, new BigDecimal("1000.00").compareTo(billAfterPayment.getBalanceAmount()), "Balance amount should be 1000.00");
        assertEquals("PARTIALLY_PAID", billAfterPayment.getStatus());

        // Step 8: Mark invoice as ISSUED. Verify that issued invoices are protected from silent overwrite
        billingService.updateBillStatus(billAfterPayment.getId(), "ISSUED");
        request.setRecalculateDraft(false);

        assertThrows(BadRequestException.class, () -> {
            billingService.generateOrRecalculateBill(request);
        }, "Should prevent silent regeneration of an ISSUED invoice");
    }
}
