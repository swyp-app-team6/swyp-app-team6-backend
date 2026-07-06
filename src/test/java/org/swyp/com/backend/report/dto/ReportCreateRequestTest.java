package org.swyp.com.backend.report.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.swyp.com.backend.global.enumeration.ReportReasonCode;

class ReportCreateRequestTest {

    static ValidatorFactory validatorFactory;
    static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void profileExchangeIdMissing_violatesNotNull() {
        ReportCreateRequest request = new ReportCreateRequest(null, List.of(ReportReasonCode.FAKE_PROFILE), null);

        Set<ConstraintViolation<ReportCreateRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("profileExchangeId"));
    }

    @Test
    void reasonCodesEmpty_violatesNotEmpty() {
        ReportCreateRequest request = new ReportCreateRequest(1L, List.of(), null);

        Set<ConstraintViolation<ReportCreateRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("reasonCodes"));
    }

    @Test
    void etcDetailOver300Chars_violatesSize() {
        ReportCreateRequest request = new ReportCreateRequest(1L, List.of(ReportReasonCode.ETC), "a".repeat(301));

        Set<ConstraintViolation<ReportCreateRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("etcDetail"));
    }

    @Test
    void validRequest_hasNoViolations() {
        ReportCreateRequest request = new ReportCreateRequest(1L, List.of(ReportReasonCode.FAKE_PROFILE), null);

        Set<ConstraintViolation<ReportCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
