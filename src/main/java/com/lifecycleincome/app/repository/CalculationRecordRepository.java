package com.lifecycleincome.app.repository;

import com.lifecycleincome.app.entity.CalculationRecord;
import com.lifecycleincome.app.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CalculationRecordRepository extends JpaRepository<CalculationRecord, Long> {
    List<CalculationRecord> findBySiteUserOrderByCreatedAtDesc(SiteUser siteUser);
}
