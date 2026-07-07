package org.swyp.com.backend.exchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "교환 후기 요청")
public record ExchangeReviewRequest(
        @NotNull(message = "평점을 입력해주세요.")
        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 4, message = "평점은 4 이하여야 합니다.")
        Integer score,

        @NotBlank(message = "후기를 입력해주세요.")
        @Size(max = 300, message = "후기는 300자 이내로 입력해주세요.")
        String review
) {
}
