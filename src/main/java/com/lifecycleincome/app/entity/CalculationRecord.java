package com.lifecycleincome.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "calculation_records")
@Getter @Setter
public class CalculationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int currentAge;
    private int retirementAge;
    private double currentAssets;
    private double annualIncome;
    private String country;

    private int workingYears;
    private int remainingLifeYears;
    private double annualConsumption;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "site_user_id")
    private SiteUser siteUser;
}
