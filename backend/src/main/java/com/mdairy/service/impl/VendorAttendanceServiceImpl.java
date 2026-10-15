package com.mdairy.service.impl;

import com.mdairy.dto.VendorAttendanceDto;
import com.mdairy.dto.DailyAttendanceSummaryDto;
import com.mdairy.entity.Vendor;
import com.mdairy.entity.VendorAttendance;
import com.mdairy.exception.BadRequestException;
import com.mdairy.exception.ResourceNotFoundException;
import com.mdairy.repository.VendorAttendanceRepository;
import com.mdairy.repository.VendorRepository;
import com.mdairy.service.VendorAttendanceService;
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
public class VendorAttendanceServiceImpl implements VendorAttendanceService {

    private final VendorAttendanceRepository attendanceRepository;
    private final VendorRepository vendorRepository;

    public VendorAttendanceServiceImpl(VendorAttendanceRepository attendanceRepository,
                                       VendorRepository vendorRepository) {
        this.attendanceRepository = attendanceRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorAttendanceDto> getAttendanceForDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        List<Vendor> vendors = vendorRepository.findByActiveOrderByVendorCodeAsc(true);
        List<VendorAttendance> savedAttendances = attendanceRepository.findByAttendanceDateOrderByVendorVendorCodeAsc(date);
        Map<Long, VendorAttendance> savedMap = savedAttendances.stream()
                .collect(Collectors.toMap(va -> va.getVendor().getId(), va -> va));

        List<VendorAttendanceDto> result = new ArrayList<>();
        for (Vendor v : vendors) {
            VendorAttendance saved = savedMap.get(v.getId());
            if (saved != null) {
                result.add(mapToDto(saved));
            } else {
                VendorAttendanceDto template = new VendorAttendanceDto();
                template.setId(null);
                template.setVendorId(v.getId());
                template.setVendorCode(v.getVendorCode());
                template.setVendorName(v.getName());
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
    public VendorAttendanceDto saveOrUpdateAttendance(VendorAttendanceDto dto) {
        if (dto.getVendorId() == null) {
            throw new BadRequestException("Vendor ID is required");
        }
        if (dto.getAttendanceDate() == null) {
            throw new BadRequestException("Attendance date is required");
        }

        validateQuantities(dto);

        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + dto.getVendorId()));

        Optional<VendorAttendance> existingOpt = attendanceRepository
                .findByVendorIdAndAttendanceDate(dto.getVendorId(), dto.getAttendanceDate());

        VendorAttendance attendance;
        if (existingOpt.isPresent()) {
            attendance = existingOpt.get();
        } else {
            attendance = new VendorAttendance();
            attendance.setVendor(vendor);
            attendance.setAttendanceDate(dto.getAttendanceDate());
        }

        attendance.setStatus(dto.getStatus() != null ? dto.getStatus().toUpperCase() : "ABSENT");

        if ("PRESENT".equalsIgnoreCase(attendance.getStatus())) {
            attendance.setMorningCowQty(dto.getMorningCowQty() != null ? dto.getMorningCowQty() : BigDecimal.ZERO);
            attendance.setMorningBuffaloQty(dto.getMorningBuffaloQty() != null ? dto.getMorningBuffaloQty() : BigDecimal.ZERO);
            attendance.setNightCowQty(dto.getNightCowQty() != null ? dto.getNightCowQty() : BigDecimal.ZERO);
            attendance.setNightBuffaloQty(dto.getNightBuffaloQty() != null ? dto.getNightBuffaloQty() : BigDecimal.ZERO);
        } else {
            attendance.setMorningCowQty(BigDecimal.ZERO);
            attendance.setMorningBuffaloQty(BigDecimal.ZERO);
            attendance.setNightCowQty(BigDecimal.ZERO);
            attendance.setNightBuffaloQty(BigDecimal.ZERO);
        }

        attendance.setNotes(dto.getNotes());
        VendorAttendance saved = attendanceRepository.save(attendance);
        return mapToDto(saved);
    }

    @Override
    public List<VendorAttendanceDto> saveBatchAttendance(List<VendorAttendanceDto> attendanceList) {
        List<VendorAttendanceDto> savedList = new ArrayList<>();
        for (VendorAttendanceDto dto : attendanceList) {
            savedList.add(saveOrUpdateAttendance(dto));
        }
        return savedList;
    }

    @Override
    @Transactional(readOnly = true)
    public DailyAttendanceSummaryDto getDailySummary(LocalDate date) {
        if (date == null) date = LocalDate.now();

        List<VendorAttendance> attendances = attendanceRepository.findByAttendanceDateOrderByVendorVendorCodeAsc(date);

        DailyAttendanceSummaryDto summary = new DailyAttendanceSummaryDto();
        summary.setDate(date);

        long presentCount = 0;
        long absentCount = 0;
        BigDecimal mCow = BigDecimal.ZERO;
        BigDecimal mBuf = BigDecimal.ZERO;
        BigDecimal nCow = BigDecimal.ZERO;
        BigDecimal nBuf = BigDecimal.ZERO;

        for (VendorAttendance a : attendances) {
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
    public List<VendorAttendanceDto> getVendorAttendanceHistory(Long vendorId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByVendorIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(vendorId, startDate, endDate)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void validateQuantities(VendorAttendanceDto dto) {
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

    private VendorAttendanceDto mapToDto(VendorAttendance va) {
        VendorAttendanceDto dto = new VendorAttendanceDto();
        dto.setId(va.getId());
        dto.setVendorId(va.getVendor().getId());
        dto.setVendorCode(va.getVendor().getVendorCode());
        dto.setVendorName(va.getVendor().getName());
        dto.setAttendanceDate(va.getAttendanceDate());
        dto.setStatus(va.getStatus());
        dto.setMorningCowQty(va.getMorningCowQty());
        dto.setMorningBuffaloQty(va.getMorningBuffaloQty());
        dto.setNightCowQty(va.getNightCowQty());
        dto.setNightBuffaloQty(va.getNightBuffaloQty());
        dto.setTotalCowQty(va.getTotalCowQty());
        dto.setTotalBuffaloQty(va.getTotalBuffaloQty());
        dto.setTotalLitres(va.getTotalLitres());
        dto.setNotes(va.getNotes());
        return dto;
    }
}
