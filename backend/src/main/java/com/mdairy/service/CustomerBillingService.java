package com.mdairy.service;

import com.mdairy.dto.BillGenerationRequest;
import com.mdairy.dto.CustomerBillDto;
import java.util.List;

public interface CustomerBillingService {
    CustomerBillDto generateOrRecalculateBill(BillGenerationRequest request);
    CustomerBillDto getBillById(Long id);
    List<CustomerBillDto> filterBills(Long customerId, Integer month, Integer year, String status);
    CustomerBillDto updateBillStatus(Long id, String status);
    void deleteDraftBill(Long id);
}
