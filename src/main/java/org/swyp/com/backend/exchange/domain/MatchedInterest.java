package org.swyp.com.backend.exchange.domain;

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
import org.swyp.com.backend.global.enumeration.InterestType;

@Entity
public class MatchedInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "exchange_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Exchange exchange;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestType type;
}
