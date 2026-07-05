package org.swyp.com.backend.region.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.RegionGroup;

@Entity
@Getter
public class RegionGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegionGroup regionGroup;

    @OneToMany(mappedBy = "regionGroupEntity", fetch = FetchType.EAGER)
    private List<RegionDetailEntity> regionDetailList = new ArrayList<>();
}
