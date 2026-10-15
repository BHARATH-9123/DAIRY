package com.mdairy.service;

import com.mdairy.dto.VendorDto;
import java.util.List;

public interface VendorService {
    List<VendorDto> getAllVendors(String search, Boolean active);
    VendorDto getVendorById(Long id);
    VendorDto createVendor(VendorDto vendorDto);
    VendorDto updateVendor(Long id, VendorDto vendorDto);
    VendorDto deactivateVendor(Long id);
    VendorDto activateVendor(Long id);
}
