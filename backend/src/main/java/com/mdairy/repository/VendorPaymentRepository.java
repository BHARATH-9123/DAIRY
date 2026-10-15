package com.mdairy.repository;

import com.mdairy.entity.VendorPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorPaymentRepository extends JpaRepository<VendorPayment, Long> {

    Optional<VendorPayment> findByPaymentNumber(String paymentNumber);

    boolean existsByPaymentNumber(String paymentNumber);

    List<VendorPayment> findByVendorIdOrderByPaymentDateDescCreatedAtDesc(Long vendorId);

    List<VendorPayment> findByBillIdOrderByPaymentDateDesc(Long billId);

    @Query("SELECT vp FROM VendorPayment vp WHERE " +
           "(:vendorId IS NULL OR vp.vendor.id = :vendorId) AND " +
           "(:startDate IS NULL OR vp.paymentDate >= :startDate) AND " +
           "(:endDate IS NULL OR vp.paymentDate <= :endDate) " +
           "ORDER BY vp.paymentDate DESC, vp.createdAt DESC")
    List<VendorPayment> filterPayments(@Param("vendorId") Long vendorId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(vp.amount), 0) FROM VendorPayment vp WHERE vp.bill.id = :billId")
    BigDecimal sumPaymentsForBill(@Param("billId") Long billId);

    @Query("SELECT COALESCE(SUM(vp.amount), 0) FROM VendorPayment vp WHERE vp.vendor.id = :vendorId")
    BigDecimal sumPaymentsForVendor(@Param("vendorId") Long vendorId);
}
