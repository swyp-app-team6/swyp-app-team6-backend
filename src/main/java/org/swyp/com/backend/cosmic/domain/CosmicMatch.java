package org.swyp.com.backend.cosmic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

@Entity
@Getter
public class CosmicMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "cosmic_id", nullable = false)
    private Cosmic cosmic;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CosmicDatingType type;

}
