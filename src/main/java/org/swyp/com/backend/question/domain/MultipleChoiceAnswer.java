package org.swyp.com.backend.question.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"multiple_choice_question_id", "answer_id"}))
@Getter
public class MultipleChoiceAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "multiple_choice_question_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private MultipleChoiceQuestion question;
    @Column(nullable = false)
    private Integer answerId;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Boolean deleted = false;
}
