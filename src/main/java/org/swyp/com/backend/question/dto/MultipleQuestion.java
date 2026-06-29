package org.swyp.com.backend.question.dto;

import java.util.List;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;

public record MultipleQuestion(
        Long id,
        CustomQuestionType type,
        String content,
        List<MultipleAnswer> answers
) {
}
