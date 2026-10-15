package com.mdairy.repository;

import com.mdairy.entity.VendorAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorAttendanceRepository extends JpaRepository<VendorAttendance, Long> {

    Optional<VendorAttendance> findByVendorIdAndAttendanceDate(Long vendorId, LocalDate attendanceDate);

    boolean existsByVendorIdAndAttendanceDate(Long vendorId, LocalDate attendanceDate);

    boolean existsByVendorIdAndAttendanceDateAndIdNot(Long vendorId, LocalDate attendanceDate, Long id);

    List<VendorAttendance> findByAttendanceDateOrderByVendorVendorCodeAsc(LocalDate attendanceDate);

    List<VendorAttendance> findByVendorIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            Long vendorId, LocalDate startDate, LocalDate endDate);

    List<VendorAttendance> findByAttendanceDateBetweenOrderByAttendanceDateAsc(
            LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(va.morningCowQty + va.morningBuffaloQty + va.nightCowQty + va.nightBuffaloQty), 0) " +
           "FROM VendorAttendance va WHERE va.attendanceDate = :date AND va.status = 'PRESENT'")
    BigDecimal sumTotalCollectionForDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(va.morningCowQty + va.nightCowQty), 0) " +
           "FROM VendorAttendance va WHERE va.attendanceDate = :date AND va.status = 'PRESENT'")
    BigDecimal sumCowCollectionForDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(va.morningBuffaloQty + va.nightBuffaloQty), 0) " +
           "FROM VendorAttendance va WHERE va.attendanceDate = :date AND va.status = 'PRESENT'")
    BigDecimal sumBuffaloCollectionForDate(@Param("date") LocalDate date);
}
