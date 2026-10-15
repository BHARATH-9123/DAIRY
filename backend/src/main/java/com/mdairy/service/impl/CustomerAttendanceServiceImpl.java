package com.mdairy.service.impl;

import com.mdairy.dto.CustomerAttendanceDto;
import com.mdairy.dto.DailyAttendanceSummaryDto;
import com.mdairy.entity.Customer;
import com.mdairy.entity.CustomerAttendance;
import com.mdairy.exception.BadRequestException;
import com.mdairy.exception.ResourceNotFoundException;
import com.mdairy.repository.CustomerAttendanceRepository;
import com.mdairy.repository.CustomerRepository;
import com.mdairy.service.CustomerAttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerAttendanceServiceImpl implements CustomerAttendanceService {

    private final CustomerAttendanceRepository attendanceRepository;
    private final CustomerRepository customerRepository;

    public CustomerAttendanceServiceImpl(CustomerAttendanceRepository attendanceRepository,
                                         CustomerRepository customerRepository) {
        this.attendanceRepository = attendanceRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAttendanceDto> getAttendanceForDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        List<Customer> customers = customerRepository.findByActiveOrderByCustomerCodeAsc(true);
        List<CustomerAttendance> savedAttendances = attendanceRepository.findByAttendanceDateOrderByCustomerCustomerCodeAsc(date);
        Map<Long, CustomerAttendance> savedMap = savedAttendances.stream()
                .collect(Collectors.toMap(ca -> ca.getCustomer().getId(), ca -> ca));

        List<CustomerAttendanceDto> result = new ArrayList<>();
        for (Customer c : customers) {
            CustomerAttendance saved = savedMap.get(c.getId());
            if (saved != null) {
                result.add(mapToDto(saved));
            } else {
                // Return unsaved template initialized to ABSENT
                CustomerAttendanceDto template = new CustomerAttendanceDto();
                template.setId(null);
                template.setCustomerId(c.getId());
                template.setCustomerCode(c.getCustomerCode());
                template.setCustomerName(c.getName());
                template.setAttendanceDate(date);
                template.setStatus("ABSENT");
                template.setMorningCowQty(BigDecimal.ZERO);
                template.setMorningBuffaloQty(BigDecimal.ZERO);
                template.setNightCowQty(BigDecimal.ZERO);
                template.setNightBuffaloQty(BigDecimal.ZERO);
                template.setTotalCowQty(BigDecimal.ZERO);
                template.setTotalBuffaloQty(BigDecimal.ZERO);
                template.setTotalLitres(BigDecimal.ZERO);
                result.add(template);
            }
        }
        return result;
    }

    @Override
    public CustomerAttendanceDto saveOrUpdateAttendance(CustomerAttendanceDto dto) {
        if (dto.getCustomerId() == null) {
            throw new BadRequestException("Customer ID is required");
        }
        if (dto.getAttendanceDate() == null) {
            throw new BadRequestException("Attendance date is required");
        }

        validateQuantities(dto);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        Optional<CustomerAttendance> existingOpt = attendanceRepository
                .findByCustomerIdAndAttendanceDate(dto.getCustomerId(), dto.getAttendanceDate());

        CustomerAttendance attendance;
        if (existingOpt.isPresent()) {
            attendance = existingOpt.get();
        } else {
            attendance = new CustomerAttendance();
            attendance.setCustomer(customer);
            attendance.setAttendanceDate(dto.getAttendanceDate());
        }

        attendance.setStatus(dto.getStatus() != null ? dto.getStatus().toUpperCase() : "ABSENT");

        if ("PRESENT".equalsIgnoreCase(attendance.getStatus())) {
            attendance.setMorningCowQty(dto.getMorningCowQty() != null ? dto.getMorningCowQty() : BigDecimal.ZERO);
            attendance.setMorningBuffaloQty(dto.getMorningBuffaloQty() != null ? dto.getMorningBuffaloQty() : BigDecimal.ZERO);
            attendance.setNightCowQty(dto.getNightCowQty() != null ? dto.getNightCowQty() : BigDecimal.ZERO);
            attendance.setNightBuffaloQty(dto.getNightBuffaloQty() != null ? dto.getNightBuffaloQty() : BigDecimal.ZERO);
        } else {
            // Absent attendance contributes zero billable litres
            attendance.setMorningCowQty(BigDecimal.ZERO);
            attendance.setMorningBuffaloQty(BigDecimal.ZERO);
            attendance.setNightCowQty(BigDecimal.ZERO);
            attendance.setNightBuffaloQty(BigDecimal.ZERO);
        }

        attendance.setNotes(dto.getNotes());
        CustomerAttendance saved = attendanceRepository.save(attendance);
        return mapToDto(saved);
    }

    @Override
    public List<CustomerAttendanceDto> saveBatchAttendance(List<CustomerAttendanceDto> attendanceList) {
        List<CustomerAttendanceDto> savedList = new ArrayList<>();
        for (CustomerAttendanceDto dto : attendanceList) {
            savedList.add(saveOrUpdateAttendance(dto));
        }
        return savedList;
    }

    @Override
    @Transactional(readOnly = true)
    public DailyAttendanceSummaryDto getDailySummary(LocalDate date) {
        if (date == null) date = LocalDate.now();

        List<CustomerAttendance> attendances = attendanceRepository.findByAttendanceDateOrderByCustomerCustomerCodeAsc(date);

        DailyAttendanceSummaryDto summary = new DailyAttendanceSummaryDto();
        summary.setDate(date);

        long presentCount = 0;
        long absentCount = 0;
        BigDecimal mCow = BigDecimal.ZERO;
        BigDecimal mBuf = BigDecimal.ZERO;
        BigDecimal nCow = BigDecimal.ZERO;
        BigDecimal nBuf = BigDecimal.ZERO;

        for (CustomerAttendance a : attendances) {
            if ("PRESENT".equalsIgnoreCase(a.getStatus())) {
                presentCount++;
                if (a.getMorningCowQty() != null) mCow = mCow.add(a.getMorningCowQty());
                if (a.getMorningBuffaloQty() != null) mBuf = mBuf.add(a.getMorningBuffaloQty());
                if (a.getNightCowQty() != null) nCow = nCow.add(a.getNightCowQty());
                if (a.getNightBuffaloQty() != null) nBuf = nBuf.add(a.getNightBuffaloQty());
            } else {
                absentCount++;
            }
        }

        summary.setTotalPresent(presentCount);
        summary.setTotalAbsent(absentCount);
        summary.setMorningCowQty(mCow);
        summary.setMorningBuffaloQty(mBuf);
        summary.setNightCowQty(nCow);
        summary.setNightBuffaloQty(nBuf);
        summary.setTotalCowQty(mCow.add(nCow));
        summary.setTotalBuffaloQty(mBuf.add(nBuf));
        summary.setTotalLitres(mCow.add(nCow).add(mBuf).add(nBuf));

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAttendanceDto> getCustomerAttendanceHistory(Long customerId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByCustomerIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(customerId, startDate, endDate)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void validateQuantities(CustomerAttendanceDto dto) {
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

    private CustomerAttendanceDto mapToDto(CustomerAttendance ca) {
        CustomerAttendanceDto dto = new CustomerAttendanceDto();
        dto.setId(ca.getId());
        dto.setCustomerId(ca.getCustomer().getId());
        dto.setCustomerCode(ca.getCustomer().getCustomerCode());
        dto.setCustomerName(ca.getCustomer().getName());
        dto.setAttendanceDate(ca.getAttendanceDate());
        dto.setStatus(ca.getStatus());
        dto.setMorningCowQty(ca.getMorningCowQty());
        dto.setMorningBuffaloQty(ca.getMorningBuffaloQty());
        dto.setNightCowQty(ca.getNightCowQty());
        dto.setNightBuffaloQty(ca.getNightBuffaloQty());
        dto.setTotalCowQty(ca.getTotalCowQty());
        dto.setTotalBuffaloQty(ca.getTotalBuffaloQty());
        dto.setTotalLitres(ca.getTotalLitres());
        dto.setNotes(ca.getNotes());
        return dto;
    }
}
