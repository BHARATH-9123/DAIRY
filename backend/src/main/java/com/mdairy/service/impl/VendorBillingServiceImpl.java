package com.mdairy.service.impl;

import com.mdairy.dto.BillGenerationRequest;
import com.mdairy.dto.VendorBillDto;
import com.mdairy.dto.VendorBillItemDto;
import com.mdairy.entity.Vendor;
import com.mdairy.entity.VendorAttendance;
import com.mdairy.entity.VendorBill;
import com.mdairy.entity.VendorBillItem;
import com.mdairy.exception.BadRequestException;
import com.mdairy.exception.ResourceNotFoundException;
import com.mdairy.repository.VendorAttendanceRepository;
import com.mdairy.repository.VendorBillItemRepository;
import com.mdairy.repository.VendorBillRepository;
import com.mdairy.repository.VendorPaymentRepository;
import com.mdairy.repository.VendorRepository;
import com.mdairy.service.VendorBillingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VendorBillingServiceImpl implements VendorBillingService {

    private final VendorBillRepository billRepository;
    private final VendorBillItemRepository billItemRepository;
    private final VendorRepository vendorRepository;
    private final VendorAttendanceRepository attendanceRepository;
    private final VendorPaymentRepository paymentRepository;

    public VendorBillingServiceImpl(VendorBillRepository billRepository,
                                    VendorBillItemRepository billItemRepository,
                                    VendorRepository vendorRepository,
                                    VendorAttendanceRepository attendanceRepository,
                                    VendorPaymentRepository paymentRepository) {
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.vendorRepository = vendorRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public VendorBillDto generateOrRecalculateBill(BillGenerationRequest request) {
        if (request.getId() == null) {
            throw new BadRequestException("Vendor ID is required");
        }
        if (request.getBillingMonth() == null || request.getBillingMonth() < 1 || request.getBillingMonth() > 12) {
            throw new BadRequestException("Valid billing month (1-12) is required");
        }
        if (request.getBillingYear() == null || request.getBillingYear() < 2000) {
            throw new BadRequestException("Valid billing year is required");
        }

        Vendor vendor = vendorRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + request.getId()));

        Optional<VendorBill> existingBillOpt = billRepository
                .findByVendorIdAndBillingMonthAndBillingYear(vendor.getId(), request.getBillingMonth(), request.getBillingYear());

        VendorBill bill;
        if (existingBillOpt.isPresent()) {
            bill = existingBillOpt.get();
            if (!"DRAFT".equalsIgnoreCase(bill.getStatus()) && !Boolean.TRUE.equals(request.getRecalculateDraft())) {
                throw new BadRequestException("Bill " + bill.getBillNumber() + " has already been " + bill.getStatus() +
                        ". Issued bills cannot be silently overwritten.");
            }
        } else {
            bill = new VendorBill();
            bill.setVendor(vendor);
            bill.setBillingMonth(request.getBillingMonth());
            bill.setBillingYear(request.getBillingYear());
            bill.setStatus("DRAFT");
            String billNumber = String.format("INV-V-%d%02d-%s",
                    request.getBillingYear(),
                    request.getBillingMonth(),
                    vendor.getVendorCode().replaceAll("[^a-zA-Z0-9]", ""));
            bill.setBillNumber(billNumber);
            bill.setPaidAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }

        // Lock in procurement rate snapshot (never customer selling rates!)
        BigDecimal cowRate = vendor.getCowRate() != null ? vendor.getCowRate() : BigDecimal.ZERO;
        BigDecimal buffaloRate = vendor.getBuffaloRate() != null ? vendor.getBuffaloRate() : BigDecimal.ZERO;
        bill.setCowRate(cowRate.setScale(2, RoundingMode.HALF_UP));
        bill.setBuffaloRate(buffaloRate.setScale(2, RoundingMode.HALF_UP));

        // Fetch eligible collections for the month
        YearMonth yearMonth = YearMonth.of(request.getBillingYear(), request.getBillingMonth());
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<VendorAttendance> attendances = attendanceRepository
                .findByVendorIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(vendor.getId(), startDate, endDate);

        BigDecimal totalCowLitres = BigDecimal.ZERO;
        BigDecimal totalBufLitres = BigDecimal.ZERO;
        BigDecimal totalCowAmount = BigDecimal.ZERO;
        BigDecimal totalBufAmount = BigDecimal.ZERO;

        List<VendorBillItem> items = new ArrayList<>();

        for (VendorAttendance att : attendances) {
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

                VendorBillItem item = new VendorBillItem();
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

        // Reconcile payments
        BigDecimal paid = BigDecimal.ZERO;
        if (bill.getId() != null) {
            paid = paymentRepository.sumPaymentsForBill(bill.getId());
            if (paid == null) paid = BigDecimal.ZERO;
        }
        bill.setPaidAmount(paid.setScale(2, RoundingMode.HALF_UP));
        bill.setBalanceAmount(totalBillAmount.subtract(paid).setScale(2, RoundingMode.HALF_UP));

        bill.getItems().clear();
        for (VendorBillItem it : items) {
            bill.getItems().add(it);
        }

        VendorBill savedBill = billRepository.save(bill);
        return mapToDto(savedBill, true);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorBillDto getBillById(Long id) {
        VendorBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id: " + id));
        return mapToDto(bill, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorBillDto> filterBills(Long vendorId, Integer month, Integer year, String status) {
        String st = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : null;
        return billRepository.filterBills(vendorId, month, year, st).stream()
                .map(b -> mapToDto(b, false))
                .collect(Collectors.toList());
    }

    @Override
    public VendorBillDto updateBillStatus(Long id, String status) {
        VendorBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id: " + id));

        String newStatus = status != null ? status.trim().toUpperCase() : "DRAFT";
        bill.setStatus(newStatus);
        return mapToDto(billRepository.save(bill), true);
    }

    @Override
    public void deleteDraftBill(Long id) {
        VendorBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor bill not found with id: " + id));

        if (!"DRAFT".equalsIgnoreCase(bill.getStatus())) {
            throw new BadRequestException("Cannot delete bill with status " + bill.getStatus() + ". Only DRAFT bills can be deleted.");
        }
        billRepository.delete(bill);
    }

    private VendorBillDto mapToDto(VendorBill bill, boolean includeItems) {
        VendorBillDto dto = new VendorBillDto();
        dto.setId(bill.getId());
        dto.setBillNumber(bill.getBillNumber());
        dto.setVendorId(bill.getVendor().getId());
        dto.setVendorCode(bill.getVendor().getVendorCode());
        dto.setVendorName(bill.getVendor().getName());
        dto.setVendorPhone(bill.getVendor().getPhone());
        dto.setVendorAddress(bill.getVendor().getAddress());
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
            List<VendorBillItemDto> itemDtos = bill.getItems().stream().map(it -> {
                VendorBillItemDto itemDto = new VendorBillItemDto();
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
