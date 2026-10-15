package com.mdairy.repository;

import com.mdairy.entity.CustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {

    Optional<CustomerPayment> findByPaymentNumber(String paymentNumber);

    boolean existsByPaymentNumber(String paymentNumber);

    List<CustomerPayment> findByCustomerIdOrderByPaymentDateDescCreatedAtDesc(Long customerId);

    List<CustomerPayment> findByBillIdOrderByPaymentDateDesc(Long billId);

    @Query("SELECT cp FROM CustomerPayment cp WHERE " +
           "(:customerId IS NULL OR cp.customer.id = :customerId) AND " +
           "(:startDate IS NULL OR cp.paymentDate >= :startDate) AND " +
           "(:endDate IS NULL OR cp.paymentDate <= :endDate) " +
           "ORDER BY cp.paymentDate DESC, cp.createdAt DESC")
    List<CustomerPayment> filterPayments(@Param("customerId") Long customerId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(cp.amount), 0) FROM CustomerPayment cp WHERE cp.bill.id = :billId")
    BigDecimal sumPaymentsForBill(@Param("billId") Long billId);

    @Query("SELECT COALESCE(SUM(cp.amount), 0) FROM CustomerPayment cp WHERE cp.customer.id = :customerId")
    BigDecimal sumPaymentsForCustomer(@Param("customerId") Long customerId);
}
