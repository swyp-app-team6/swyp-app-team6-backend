package org.swyp.com.backend.profile.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import org.swyp.com.backend.question.domain.MultipleChoiceAnswer;

@Entity
@Getter
public class ProfileChoiceTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "profile_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Profile profile;
    @JoinColumn(name = "multiple_choice_answer_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private MultipleChoiceAnswer answer;

    public static ProfileChoiceTemplate createProfileChoiceTemplate(Profile profile, MultipleChoiceAnswer answer) {
        ProfileChoiceTemplate profileChoiceTemplate = new ProfileChoiceTemplate();
        profileChoiceTemplate.profile = profile;
        profileChoiceTemplate.answer = answer;
        return profileChoiceTemplate;
    }
}
