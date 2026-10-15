package com.mdairy.repository;

import com.mdairy.entity.BusinessSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessSettingsRepository extends JpaRepository<BusinessSettings, Long> {
}
