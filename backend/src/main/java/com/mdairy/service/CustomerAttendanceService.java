package com.mdairy.service;

import com.mdairy.dto.CustomerAttendanceDto;
import com.mdairy.dto.DailyAttendanceSummaryDto;
import java.time.LocalDate;
import java.util.List;

public interface CustomerAttendanceService {
    List<CustomerAttendanceDto> getAttendanceForDate(LocalDate date);
    CustomerAttendanceDto saveOrUpdateAttendance(CustomerAttendanceDto attendanceDto);
    List<CustomerAttendanceDto> saveBatchAttendance(List<CustomerAttendanceDto> attendanceList);
    DailyAttendanceSummaryDto getDailySummary(LocalDate date);
    List<CustomerAttendanceDto> getCustomerAttendanceHistory(Long customerId, LocalDate startDate, LocalDate endDate);
}
