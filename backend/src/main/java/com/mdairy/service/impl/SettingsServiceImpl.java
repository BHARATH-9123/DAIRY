package com.mdairy.service.impl;

import com.mdairy.dto.BusinessSettingsDto;
import com.mdairy.entity.BusinessSettings;
import com.mdairy.repository.BusinessSettingsRepository;
import com.mdairy.service.SettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class SettingsServiceImpl implements SettingsService {

    private final BusinessSettingsRepository settingsRepository;

    public SettingsServiceImpl(BusinessSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessSettingsDto getSettings() {
        BusinessSettings settings = settingsRepository.findById(1L).orElseGet(() -> {
            BusinessSettings s = new BusinessSettings();
            s.setId(1L);
            return settingsRepository.save(s);
        });
        return mapToDto(settings);
    }

    @Override
    public BusinessSettingsDto updateSettings(BusinessSettingsDto dto) {
        BusinessSettings settings = settingsRepository.findById(1L).orElseGet(() -> {
            BusinessSettings s = new BusinessSettings();
            s.setId(1L);
            return s;
        });

        settings.setBusinessName(dto.getBusinessName());
        settings.setTagline(dto.getTagline());
        settings.setAddress(dto.getAddress());
        settings.setPhone(dto.getPhone());
        settings.setEmail(dto.getEmail());
        if (dto.getDefaultCowSellingRate() != null) settings.setDefaultCowSellingRate(dto.getDefaultCowSellingRate());
        if (dto.getDefaultBuffaloSellingRate() != null) settings.setDefaultBuffaloSellingRate(dto.getDefaultBuffaloSellingRate());
        if (dto.getDefaultCowProcurementRate() != null) settings.setDefaultCowProcurementRate(dto.getDefaultCowProcurementRate());
        if (dto.getDefaultBuffaloProcurementRate() != null) settings.setDefaultBuffaloProcurementRate(dto.getDefaultBuffaloProcurementRate());
        if (dto.getCurrencySymbol() != null) settings.setCurrencySymbol(dto.getCurrencySymbol());
        if (dto.getQuantityPrecision() != null) settings.setQuantityPrecision(dto.getQuantityPrecision());

        BusinessSettings saved = settingsRepository.save(settings);
        return mapToDto(saved);
    }

    private BusinessSettingsDto mapToDto(BusinessSettings s) {
        BusinessSettingsDto dto = new BusinessSettingsDto();
        dto.setId(s.getId());
        dto.setBusinessName(s.getBusinessName());
        dto.setTagline(s.getTagline());
        dto.setAddress(s.getAddress());
        dto.setPhone(s.getPhone());
        dto.setEmail(s.getEmail());
        dto.setDefaultCowSellingRate(s.getDefaultCowSellingRate());
        dto.setDefaultBuffaloSellingRate(s.getDefaultBuffaloSellingRate());
        dto.setDefaultCowProcurementRate(s.getDefaultCowProcurementRate());
        dto.setDefaultBuffaloProcurementRate(s.getDefaultBuffaloProcurementRate());
        dto.setCurrencySymbol(s.getCurrencySymbol());
        dto.setQuantityPrecision(s.getQuantityPrecision());
        dto.setUpdatedAt(s.getUpdatedAt());
        return dto;
    }
}
