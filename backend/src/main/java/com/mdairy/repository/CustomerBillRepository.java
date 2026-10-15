package com.mdairy.repository;

import com.mdairy.entity.CustomerBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerBillRepository extends JpaRepository<CustomerBill, Long> {

    Optional<CustomerBill> findByCustomerIdAndBillingMonthAndBillingYear(Long customerId, Integer billingMonth, Integer billingYear);

    Optional<CustomerBill> findByBillNumber(String billNumber);

    boolean existsByCustomerIdAndBillingMonthAndBillingYear(Long customerId, Integer billingMonth, Integer billingYear);

    boolean existsByBillNumber(String billNumber);

    List<CustomerBill> findByCustomerIdOrderByBillingYearDescBillingMonthDesc(Long customerId);

    List<CustomerBill> findByBillingMonthAndBillingYearOrderByCustomerCustomerCodeAsc(Integer billingMonth, Integer billingYear);

    @Query("SELECT cb FROM CustomerBill cb WHERE " +
           "(:customerId IS NULL OR cb.customer.id = :customerId) AND " +
           "(:billingMonth IS NULL OR cb.billingMonth = :billingMonth) AND " +
           "(:billingYear IS NULL OR cb.billingYear = :billingYear) AND " +
           "(:status IS NULL OR cb.status = :status) " +
           "ORDER BY cb.billingYear DESC, cb.billingMonth DESC, cb.customer.customerCode ASC")
    List<CustomerBill> filterBills(@Param("customerId") Long customerId,
                                   @Param("billingMonth") Integer billingMonth,
                                   @Param("billingYear") Integer billingYear,
                                   @Param("status") String status);

    @Query("SELECT COALESCE(SUM(cb.totalAmount), 0) FROM CustomerBill cb WHERE cb.billingMonth = :month AND cb.billingYear = :year")
    BigDecimal sumTotalBilledForMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT COALESCE(SUM(cb.balanceAmount), 0) FROM CustomerBill cb WHERE cb.balanceAmount > 0")
    BigDecimal sumTotalOutstandingBalance();
}
