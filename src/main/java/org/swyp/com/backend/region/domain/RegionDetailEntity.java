package org.swyp.com.backend.region.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.RegionDetail;

@Entity
@Getter
public class RegionDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "region_group_entity_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RegionGroupEntity regionGroupEntity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegionDetail regionDetail;
}
