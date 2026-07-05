package org.swyp.com.backend.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.swyp.com.backend.global.enumeration.WithdrawalReasonCode;

class UserWithdrawalRequestTest {

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
    void reasonCodeMissing_violatesNotNull() {
        // given
        UserWithdrawalRequest request = new UserWithdrawalRequest(null, null);

        // when
        Set<ConstraintViolation<UserWithdrawalRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("reasonCode"));
    }

    @Test
    void reasonDetailOver300Chars_violatesSize() {
        // given
        UserWithdrawalRequest request = new UserWithdrawalRequest(WithdrawalReasonCode.ETC, "a".repeat(301));

        // when
        Set<ConstraintViolation<UserWithdrawalRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("reasonDetail"));
    }

    @Test
    void reasonDetailWithin300Chars_isValid() {
        // given
        UserWithdrawalRequest request = new UserWithdrawalRequest(WithdrawalReasonCode.ETC, "a".repeat(300));

        // when
        Set<ConstraintViolation<UserWithdrawalRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }
}
