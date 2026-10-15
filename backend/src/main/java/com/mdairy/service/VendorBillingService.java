package com.mdairy.service;

import com.mdairy.dto.BillGenerationRequest;
import com.mdairy.dto.VendorBillDto;
import java.util.List;

public interface VendorBillingService {
    VendorBillDto generateOrRecalculateBill(BillGenerationRequest request);
    VendorBillDto getBillById(Long id);
    List<VendorBillDto> filterBills(Long vendorId, Integer month, Integer year, String status);
    VendorBillDto updateBillStatus(Long id, String status);
    void deleteDraftBill(Long id);
}
