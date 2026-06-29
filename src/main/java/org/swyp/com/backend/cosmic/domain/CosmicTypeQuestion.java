package org.swyp.com.backend.cosmic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

@Entity
@Table(name = "dating_type_question")
@Getter
public class CosmicTypeQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer questionId;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Integer answerId;
    @Column(nullable = false)
    private String answer;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CosmicDatingType cosmic;
    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Boolean deleted = false;
}
