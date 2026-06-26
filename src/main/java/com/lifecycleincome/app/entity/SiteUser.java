package com.lifecycleincome.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "site_users")
@Getter @Setter
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "siteUser", cascade = CascadeType.REMOVE)
    private List<CalculationRecord> calculationRecords = new ArrayList<>();
}
