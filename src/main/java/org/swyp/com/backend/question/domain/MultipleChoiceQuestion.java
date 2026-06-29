package org.swyp.com.backend.question.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;

@Entity
@Getter
public class MultipleChoiceQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomQuestionType type;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Boolean deleted = false;
}
