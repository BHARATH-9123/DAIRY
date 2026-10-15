package com.mdairy.service.impl;

import com.mdairy.dto.BillGenerationRequest;
import com.mdairy.dto.CustomerBillDto;
import com.mdairy.dto.CustomerBillItemDto;
import com.mdairy.entity.Customer;
import com.mdairy.entity.CustomerAttendance;
import com.mdairy.entity.CustomerBill;
import com.mdairy.entity.CustomerBillItem;
import com.mdairy.exception.BadRequestException;
import com.mdairy.exception.ResourceNotFoundException;
import com.mdairy.repository.CustomerAttendanceRepository;
import com.mdairy.repository.CustomerBillItemRepository;
import com.mdairy.repository.CustomerBillRepository;
import com.mdairy.repository.CustomerPaymentRepository;
import com.mdairy.repository.CustomerRepository;
import com.mdairy.service.CustomerBillingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerBillingServiceImpl implements CustomerBillingService {

    private final CustomerBillRepository billRepository;
    private final CustomerBillItemRepository billItemRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAttendanceRepository attendanceRepository;
    private final CustomerPaymentRepository paymentRepository;

    public CustomerBillingServiceImpl(CustomerBillRepository billRepository,
                                      CustomerBillItemRepository billItemRepository,
                                      CustomerRepository customerRepository,
                                      CustomerAttendanceRepository attendanceRepository,
                                      CustomerPaymentRepository paymentRepository) {
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.customerRepository = customerRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public CustomerBillDto generateOrRecalculateBill(BillGenerationRequest request) {
        if (request.getId() == null) {
            throw new BadRequestException("Customer ID is required");
        }
        if (request.getBillingMonth() == null || request.getBillingMonth() < 1 || request.getBillingMonth() > 12) {
            throw new BadRequestException("Valid billing month (1-12) is required");
        }
        if (request.getBillingYear() == null || request.getBillingYear() < 2000) {
            throw new BadRequestException("Valid billing year is required");
        }

        Customer customer = customerRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getId()));

        Optional<CustomerBill> existingBillOpt = billRepository
                .findByCustomerIdAndBillingMonthAndBillingYear(customer.getId(), request.getBillingMonth(), request.getBillingYear());

        CustomerBill bill;
        if (existingBillOpt.isPresent()) {
            bill = existingBillOpt.get();
            if (!"DRAFT".equalsIgnoreCase(bill.getStatus()) && !Boolean.TRUE.equals(request.getRecalculateDraft())) {
                throw new BadRequestException("Invoice " + bill.getBillNumber() + " has already been " + bill.getStatus() +
                        ". Issued invoices cannot be silently overwritten.");
            }
        } else {
            bill = new CustomerBill();
            bill.setCustomer(customer);
            bill.setBillingMonth(request.getBillingMonth());
            bill.setBillingYear(request.getBillingYear());
            bill.setStatus("DRAFT");
            String billNumber = String.format("INV-C-%d%02d-%s",
                    request.getBillingYear(),
                    request.getBillingMonth(),
                    customer.getCustomerCode().replaceAll("[^a-zA-Z0-9]", ""));
            bill.setBillNumber(billNumber);
            bill.setPaidAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }

        // Lock in rate snapshot
        BigDecimal cowRate = customer.getCowRate() != null ? customer.getCowRate() : BigDecimal.ZERO;
        BigDecimal buffaloRate = customer.getBuffaloRate() != null ? customer.getBuffaloRate() : BigDecimal.ZERO;
        bill.setCowRate(cowRate.setScale(2, RoundingMode.HALF_UP));
        bill.setBuffaloRate(buffaloRate.setScale(2, RoundingMode.HALF_UP));

        // Fetch eligible attendance for the month
        YearMonth yearMonth = YearMonth.of(request.getBillingYear(), request.getBillingMonth());
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<CustomerAttendance> attendances = attendanceRepository
                .findByCustomerIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(customer.getId(), startDate, endDate);

        // Sum up eligible saved attendance (status PRESENT)
        BigDecimal totalCowLitres = BigDecimal.ZERO;
        BigDecimal totalBufLitres = BigDecimal.ZERO;
        BigDecimal totalCowAmount = BigDecimal.ZERO;
        BigDecimal totalBufAmount = BigDecimal.ZERO;

        List<CustomerBillItem> items = new ArrayList<>();

        for (CustomerAttendance att : attendances) {
            if ("PRESENT".equalsIgnoreCase(att.getStatus())) {
                BigDecimal mCow = att.getMorningCowQty() != null ? att.getMorningCowQty() : BigDecimal.ZERO;
                BigDecimal nCow = att.getNightCowQty() != null ? att.getNightCowQty() : BigDecimal.ZERO;
                BigDecimal dayCow = mCow.add(nCow);

                BigDecimal mBuf = att.getMorningBuffaloQty() != null ? att.getMorningBuffaloQty() : BigDecimal.ZERO;
                BigDecimal nBuf = att.getNightBuffaloQty() != null ? att.getNightBuffaloQty() : BigDecimal.ZERO;
                BigDecimal dayBuf = mBuf.add(nBuf);

                BigDecimal dayTotal = dayCow.add(dayBuf);

                BigDecimal dayCowAmt = dayCow.multiply(cowRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal dayBufAmt = dayBuf.multiply(buffaloRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal dayAmt = dayCowAmt.add(dayBufAmt);

                totalCowLitres = totalCowLitres.add(dayCow);
                totalBufLitres = totalBufLitres.add(dayBuf);
                totalCowAmount = totalCowAmount.add(dayCowAmt);
                totalBufAmount = totalBufAmount.add(dayBufAmt);

                CustomerBillItem item = new CustomerBillItem();
                item.setBill(bill);
                item.setAttendanceDate(att.getAttendanceDate());
                item.setMorningCowQty(mCow.setScale(2, RoundingMode.HALF_UP));
                item.setMorningBuffaloQty(mBuf.setScale(2, RoundingMode.HALF_UP));
                item.setNightCowQty(nCow.setScale(2, RoundingMode.HALF_UP));
                item.setNightBuffaloQty(nBuf.setScale(2, RoundingMode.HALF_UP));
                item.setDailyTotalLitres(dayTotal.setScale(2, RoundingMode.HALF_UP));
                item.setCowRate(cowRate.setScale(2, RoundingMode.HALF_UP));
                item.setBuffaloRate(buffaloRate.setScale(2, RoundingMode.HALF_UP));
                item.setDailyAmount(dayAmt);

                items.add(item);
            }
        }

        bill.setCowLitres(totalCowLitres.setScale(2, RoundingMode.HALF_UP));
        bill.setBuffaloLitres(totalBufLitres.setScale(2, RoundingMode.HALF_UP));
        bill.setTotalLitres(totalCowLitres.add(totalBufLitres).setScale(2, RoundingMode.HALF_UP));

        bill.setCowAmount(totalCowAmount.setScale(2, RoundingMode.HALF_UP));
        bill.setBuffaloAmount(totalBufAmount.setScale(2, RoundingMode.HALF_UP));
        BigDecimal totalBillAmount = totalCowAmount.add(totalBufAmount).setScale(2, RoundingMode.HALF_UP);
        bill.setTotalAmount(totalBillAmount);

        // Calculate paid amount from existing payments for this bill if already saved
        BigDecimal paid = BigDecimal.ZERO;
        if (bill.getId() != null) {
            paid = paymentRepository.sumPaymentsForBill(bill.getId());
            if (paid == null) paid = BigDecimal.ZERO;
        }
        bill.setPaidAmount(paid.setScale(2, RoundingMode.HALF_UP));
        bill.setBalanceAmount(totalBillAmount.subtract(paid).setScale(2, RoundingMode.HALF_UP));

        // Preserve items
        bill.getItems().clear();
        for (CustomerBillItem it : items) {
            bill.getItems().add(it);
        }

        CustomerBill savedBill = billRepository.save(bill);
        return mapToDto(savedBill, true);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerBillDto getBillById(Long id) {
        CustomerBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer bill not found with id: " + id));
        return mapToDto(bill, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerBillDto> filterBills(Long customerId, Integer month, Integer year, String status) {
        String st = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : null;
        return billRepository.filterBills(customerId, month, year, st).stream()
                .map(b -> mapToDto(b, false))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerBillDto updateBillStatus(Long id, String status) {
        CustomerBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer bill not found with id: " + id));

        String newStatus = status != null ? status.trim().toUpperCase() : "DRAFT";
        bill.setStatus(newStatus);
        return mapToDto(billRepository.save(bill), true);
    }

    @Override
    public void deleteDraftBill(Long id) {
        CustomerBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer bill not found with id: " + id));

        if (!"DRAFT".equalsIgnoreCase(bill.getStatus())) {
            throw new BadRequestException("Cannot delete bill with status " + bill.getStatus() + ". Only DRAFT bills can be deleted.");
        }
        billRepository.delete(bill);
    }

    private CustomerBillDto mapToDto(CustomerBill bill, boolean includeItems) {
        CustomerBillDto dto = new CustomerBillDto();
        dto.setId(bill.getId());
        dto.setBillNumber(bill.getBillNumber());
        dto.setCustomerId(bill.getCustomer().getId());
        dto.setCustomerCode(bill.getCustomer().getCustomerCode());
        dto.setCustomerName(bill.getCustomer().getName());
        dto.setCustomerPhone(bill.getCustomer().getPhone());
        dto.setCustomerAddress(bill.getCustomer().getAddress());
        dto.setBillingMonth(bill.getBillingMonth());
        dto.setBillingYear(bill.getBillingYear());
        dto.setCowLitres(bill.getCowLitres());
        dto.setBuffaloLitres(bill.getBuffaloLitres());
        dto.setTotalLitres(bill.getTotalLitres());
        dto.setCowRate(bill.getCowRate());
        dto.setBuffaloRate(bill.getBuffaloRate());
        dto.setCowAmount(bill.getCowAmount());
        dto.setBuffaloAmount(bill.getBuffaloAmount());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setPaidAmount(bill.getPaidAmount());
        dto.setBalanceAmount(bill.getBalanceAmount());
        dto.setStatus(bill.getStatus());
        dto.setNotes(bill.getNotes());
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setUpdatedAt(bill.getUpdatedAt());

        if (includeItems && bill.getItems() != null) {
            List<CustomerBillItemDto> itemDtos = bill.getItems().stream().map(it -> {
                CustomerBillItemDto itemDto = new CustomerBillItemDto();
                itemDto.setId(it.getId());
                itemDto.setAttendanceDate(it.getAttendanceDate());
                itemDto.setMorningCowQty(it.getMorningCowQty());
                itemDto.setMorningBuffaloQty(it.getMorningBuffaloQty());
                itemDto.setNightCowQty(it.getNightCowQty());
                itemDto.setNightBuffaloQty(it.getNightBuffaloQty());
                itemDto.setDailyTotalLitres(it.getDailyTotalLitres());
                itemDto.setCowRate(it.getCowRate());
                itemDto.setBuffaloRate(it.getBuffaloRate());
                itemDto.setDailyAmount(it.getDailyAmount());
                return itemDto;
            }).collect(Collectors.toList());
            dto.setItems(itemDtos);
        }
        return dto;
    }
}
