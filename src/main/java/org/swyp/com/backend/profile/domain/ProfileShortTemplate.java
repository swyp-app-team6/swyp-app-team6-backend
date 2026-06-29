package org.swyp.com.backend.profile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import org.swyp.com.backend.question.domain.ShortAnswerQuestion;

@Entity
@Getter
public class ProfileShortTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "profile_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Profile profile;
    @JoinColumn(name = "short_answer_question_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ShortAnswerQuestion question;
    @Column(nullable = false)
    private String answer;

    public static ProfileShortTemplate createProfileShortTemplate(Profile profile, ShortAnswerQuestion question,
                                                                  String answer) {
        ProfileShortTemplate profileShortTemplate = new ProfileShortTemplate();
        profileShortTemplate.profile = profile;
        profileShortTemplate.question = question;
        profileShortTemplate.answer = answer;
        return profileShortTemplate;
    }
}
