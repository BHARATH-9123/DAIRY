package com.mdairy.controller;

import com.mdairy.dto.BusinessSettingsDto;
import com.mdairy.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<BusinessSettingsDto> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    public ResponseEntity<BusinessSettingsDto> updateSettings(@Valid @RequestBody BusinessSettingsDto settingsDto) {
        return ResponseEntity.ok(settingsService.updateSettings(settingsDto));
    }
}
