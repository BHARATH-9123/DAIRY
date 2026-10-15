package com.mdairy.repository;

import com.mdairy.entity.VendorBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {

    Optional<VendorBill> findByVendorIdAndBillingMonthAndBillingYear(Long vendorId, Integer billingMonth, Integer billingYear);

    Optional<VendorBill> findByBillNumber(String billNumber);

    boolean existsByVendorIdAndBillingMonthAndBillingYear(Long vendorId, Integer billingMonth, Integer billingYear);

    boolean existsByBillNumber(String billNumber);

    List<VendorBill> findByVendorIdOrderByBillingYearDescBillingMonthDesc(Long vendorId);

    List<VendorBill> findByBillingMonthAndBillingYearOrderByVendorVendorCodeAsc(Integer billingMonth, Integer billingYear);

    @Query("SELECT vb FROM VendorBill vb WHERE " +
           "(:vendorId IS NULL OR vb.vendor.id = :vendorId) AND " +
           "(:billingMonth IS NULL OR vb.billingMonth = :billingMonth) AND " +
           "(:billingYear IS NULL OR vb.billingYear = :billingYear) AND " +
           "(:status IS NULL OR vb.status = :status) " +
           "ORDER BY vb.billingYear DESC, vb.billingMonth DESC, vb.vendor.vendorCode ASC")
    List<VendorBill> filterBills(@Param("vendorId") Long vendorId,
                                 @Param("billingMonth") Integer billingMonth,
                                 @Param("billingYear") Integer billingYear,
                                 @Param("status") String status);

    @Query("SELECT COALESCE(SUM(vb.totalAmount), 0) FROM VendorBill vb WHERE vb.billingMonth = :month AND vb.billingYear = :year")
    BigDecimal sumTotalBilledForMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT COALESCE(SUM(vb.balanceAmount), 0) FROM VendorBill vb WHERE vb.balanceAmount > 0")
    BigDecimal sumTotalOutstandingBalance();
}
