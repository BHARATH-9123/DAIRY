package com.mdairy.controller;

import com.mdairy.dto.VendorAttendanceDto;
import com.mdairy.dto.DailyAttendanceSummaryDto;
import com.mdairy.service.VendorAttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vendor-attendance")
public class VendorAttendanceController {

    private final VendorAttendanceService attendanceService;

    public VendorAttendanceController(VendorAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<List<VendorAttendanceDto>> getAttendanceForDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return ResponseEntity.ok(attendanceService.getAttendanceForDate(date));
    }

    @PostMapping
    public ResponseEntity<VendorAttendanceDto> saveAttendance(@Valid @RequestBody VendorAttendanceDto attendanceDto) {
        return ResponseEntity.ok(attendanceService.saveOrUpdateAttendance(attendanceDto));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<VendorAttendanceDto>> saveBatchAttendance(@Valid @RequestBody List<VendorAttendanceDto> attendanceList) {
        return ResponseEntity.ok(attendanceService.saveBatchAttendance(attendanceList));
    }

    @GetMapping("/summary")
    public ResponseEntity<DailyAttendanceSummaryDto> getDailySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return ResponseEntity.ok(attendanceService.getDailySummary(date));
    }

    @GetMapping("/history/{vendorId}")
    public ResponseEntity<List<VendorAttendanceDto>> getVendorAttendanceHistory(
            @PathVariable Long vendorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getVendorAttendanceHistory(vendorId, startDate, endDate));
    }
}
