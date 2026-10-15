package com.mdairy.service;

import com.mdairy.dto.VendorAttendanceDto;
import com.mdairy.dto.DailyAttendanceSummaryDto;
import java.time.LocalDate;
import java.util.List;

public interface VendorAttendanceService {
    List<VendorAttendanceDto> getAttendanceForDate(LocalDate date);
    VendorAttendanceDto saveOrUpdateAttendance(VendorAttendanceDto attendanceDto);
    List<VendorAttendanceDto> saveBatchAttendance(List<VendorAttendanceDto> attendanceList);
    DailyAttendanceSummaryDto getDailySummary(LocalDate date);
    List<VendorAttendanceDto> getVendorAttendanceHistory(Long vendorId, LocalDate startDate, LocalDate endDate);
}
