package com.mdairy.repository;

import com.mdairy.entity.CustomerBillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerBillItemRepository extends JpaRepository<CustomerBillItem, Long> {
    List<CustomerBillItem> findByBillIdOrderByAttendanceDateAsc(Long billId);
    void deleteByBillId(Long billId);
}
