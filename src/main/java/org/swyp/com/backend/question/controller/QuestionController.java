package org.swyp.com.backend.question.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.question.controller.api.QuestionControllerApiSpec;
import org.swyp.com.backend.question.dto.CustomQuestionResponse;
import org.swyp.com.backend.question.service.QuestionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController implements QuestionControllerApiSpec {
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<CustomQuestionResponse> getCustomQuestionResponse() {

        CustomQuestionResponse customQuestionResponse = questionService.getCustomQuestionResponse();
        return ResponseEntity.ok(customQuestionResponse);

    }
}
