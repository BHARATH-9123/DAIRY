package com.mdairy.repository;

import com.mdairy.entity.CustomerAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAttendanceRepository extends JpaRepository<CustomerAttendance, Long> {

    Optional<CustomerAttendance> findByCustomerIdAndAttendanceDate(Long customerId, LocalDate attendanceDate);

    boolean existsByCustomerIdAndAttendanceDate(Long customerId, LocalDate attendanceDate);

    boolean existsByCustomerIdAndAttendanceDateAndIdNot(Long customerId, LocalDate attendanceDate, Long id);

    List<CustomerAttendance> findByAttendanceDateOrderByCustomerCustomerCodeAsc(LocalDate attendanceDate);

    List<CustomerAttendance> findByCustomerIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            Long customerId, LocalDate startDate, LocalDate endDate);

    List<CustomerAttendance> findByAttendanceDateBetweenOrderByAttendanceDateAsc(
            LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(ca.morningCowQty + ca.morningBuffaloQty + ca.nightCowQty + ca.nightBuffaloQty), 0) " +
           "FROM CustomerAttendance ca WHERE ca.attendanceDate = :date AND ca.status = 'PRESENT'")
    BigDecimal sumTotalDeliveryForDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(ca.morningCowQty + ca.nightCowQty), 0) " +
           "FROM CustomerAttendance ca WHERE ca.attendanceDate = :date AND ca.status = 'PRESENT'")
    BigDecimal sumCowDeliveryForDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(ca.morningBuffaloQty + ca.nightBuffaloQty), 0) " +
           "FROM CustomerAttendance ca WHERE ca.attendanceDate = :date AND ca.status = 'PRESENT'")
    BigDecimal sumBuffaloDeliveryForDate(@Param("date") LocalDate date);
}
