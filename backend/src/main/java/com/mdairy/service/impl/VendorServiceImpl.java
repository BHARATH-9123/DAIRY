package com.mdairy.service.impl;

import com.mdairy.dto.VendorDto;
import com.mdairy.entity.Vendor;
import com.mdairy.exception.BadRequestException;
import com.mdairy.exception.DuplicateResourceException;
import com.mdairy.exception.ResourceNotFoundException;
import com.mdairy.repository.VendorRepository;
import com.mdairy.service.VendorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    public VendorServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorDto> getAllVendors(String search, Boolean active) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return vendorRepository.searchVendors(query, active).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VendorDto getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        return mapToDto(vendor);
    }

    @Override
    public VendorDto createVendor(VendorDto vendorDto) {
        if (vendorDto.getVendorCode() == null || vendorDto.getVendorCode().trim().isEmpty()) {
            throw new BadRequestException("Vendor code cannot be empty");
        }
        String code = vendorDto.getVendorCode().trim();
        if (vendorRepository.existsByVendorCode(code)) {
            throw new DuplicateResourceException("Vendor code '" + code + "' already exists");
        }

        validateRatesAndQuantities(vendorDto);

        Vendor vendor = new Vendor();
        mapToEntity(vendorDto, vendor);
        vendor.setVendorCode(code);
        Vendor saved = vendorRepository.save(vendor);
        return mapToDto(saved);
    }

    @Override
    public VendorDto updateVendor(Long id, VendorDto vendorDto) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));

        String code = vendorDto.getVendorCode().trim();
        if (vendorRepository.existsByVendorCodeAndIdNot(code, id)) {
            throw new DuplicateResourceException("Vendor code '" + code + "' is already in use by another vendor");
        }

        validateRatesAndQuantities(vendorDto);

        mapToEntity(vendorDto, vendor);
        vendor.setVendorCode(code);
        Vendor updated = vendorRepository.save(vendor);
        return mapToDto(updated);
    }

    @Override
    public VendorDto deactivateVendor(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        vendor.setActive(false);
        return mapToDto(vendorRepository.save(vendor));
    }

    @Override
    public VendorDto activateVendor(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        vendor.setActive(true);
        return mapToDto(vendorRepository.save(vendor));
    }

    private void validateRatesAndQuantities(VendorDto dto) {
        if (dto.getCowRate() != null && dto.getCowRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Cow procurement rate cannot be negative");
        }
        if (dto.getBuffaloRate() != null && dto.getBuffaloRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Buffalo procurement rate cannot be negative");
        }
        if (dto.getMorningCowQty() != null && dto.getMorningCowQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Morning cow quantity cannot be negative");
        }
        if (dto.getMorningBuffaloQty() != null && dto.getMorningBuffaloQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Morning buffalo quantity cannot be negative");
        }
        if (dto.getNightCowQty() != null && dto.getNightCowQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Night cow quantity cannot be negative");
        }
        if (dto.getNightBuffaloQty() != null && dto.getNightBuffaloQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Night buffalo quantity cannot be negative");
        }
    }

    private VendorDto mapToDto(Vendor vendor) {
        VendorDto dto = new VendorDto();
        dto.setId(vendor.getId());
        dto.setVendorCode(vendor.getVendorCode());
        dto.setName(vendor.getName());
        dto.setPhone(vendor.getPhone());
        dto.setAddress(vendor.getAddress());
        dto.setMilkType(vendor.getMilkType());
        dto.setCollectionSchedule(vendor.getCollectionSchedule());
        dto.setMorningCowQty(vendor.getMorningCowQty());
        dto.setMorningBuffaloQty(vendor.getMorningBuffaloQty());
        dto.setNightCowQty(vendor.getNightCowQty());
        dto.setNightBuffaloQty(vendor.getNightBuffaloQty());
        dto.setCowRate(vendor.getCowRate());
        dto.setBuffaloRate(vendor.getBuffaloRate());
        dto.setNotes(vendor.getNotes());
        dto.setActive(vendor.getActive());
        dto.setCreatedAt(vendor.getCreatedAt());
        dto.setUpdatedAt(vendor.getUpdatedAt());
        return dto;
    }

    private void mapToEntity(VendorDto dto, Vendor entity) {
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setMilkType(dto.getMilkType() != null ? dto.getMilkType() : "COW");
        entity.setCollectionSchedule(dto.getCollectionSchedule() != null ? dto.getCollectionSchedule() : "MORNING");
        entity.setMorningCowQty(dto.getMorningCowQty() != null ? dto.getMorningCowQty() : BigDecimal.ZERO);
        entity.setMorningBuffaloQty(dto.getMorningBuffaloQty() != null ? dto.getMorningBuffaloQty() : BigDecimal.ZERO);
        entity.setNightCowQty(dto.getNightCowQty() != null ? dto.getNightCowQty() : BigDecimal.ZERO);
        entity.setNightBuffaloQty(dto.getNightBuffaloQty() != null ? dto.getNightBuffaloQty() : BigDecimal.ZERO);
        entity.setCowRate(dto.getCowRate() != null ? dto.getCowRate() : BigDecimal.valueOf(45.00));
        entity.setBuffaloRate(dto.getBuffaloRate() != null ? dto.getBuffaloRate() : BigDecimal.valueOf(65.00));
        entity.setNotes(dto.getNotes());
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
