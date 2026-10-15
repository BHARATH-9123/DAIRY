package com.mdairy.repository;

import com.mdairy.entity.VendorBillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorBillItemRepository extends JpaRepository<VendorBillItem, Long> {
    List<VendorBillItem> findByBillIdOrderByAttendanceDateAsc(Long billId);
    void deleteByBillId(Long billId);
}
