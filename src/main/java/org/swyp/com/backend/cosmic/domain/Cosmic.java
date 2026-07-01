package org.swyp.com.backend.cosmic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

@Entity
@Getter
public class Cosmic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CosmicDatingType type;
    @Column(nullable = false)
    private String detail;
    @Column(nullable = false)
    private String imageKey;
    @Column(nullable = false)
    private Boolean deleted = false;
}
