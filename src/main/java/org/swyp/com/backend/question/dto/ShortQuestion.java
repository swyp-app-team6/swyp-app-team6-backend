package org.swyp.com.backend.question.dto;

import org.swyp.com.backend.global.enumeration.CustomQuestionType;

public record ShortQuestion(
        Long id,
        CustomQuestionType type,
        String content
) {
}
