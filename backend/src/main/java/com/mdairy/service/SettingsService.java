package com.mdairy.service;

import com.mdairy.dto.BusinessSettingsDto;

public interface SettingsService {
    BusinessSettingsDto getSettings();
    BusinessSettingsDto updateSettings(BusinessSettingsDto settingsDto);
}
