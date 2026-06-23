package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.swyp.com.backend.global.enumeration.InterestType;

public record ProfileUpdateRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 3, max = 10, message = "닉네임은 3~10자여야 합니다.")
        String nickname,
        @JsonProperty("image_key")
        String imageKey,
        @NotBlank(message = "소개는 필수입니다.")
        @Size(max = 20, message = "소개는 최대 20자까지 입력 가능합니다.")
        String bio,
        @NotBlank(message = "키워드는 필수입니다.")
        @Size(max = 20, message = "키워드는 최대 20자까지 입력 가능합니다.")
        String keyword,
        @NotBlank(message = "주제는 필수입니다.")
        @Size(max = 20, message = "주제는 최대 20자까지 입력 가능합니다.")
        String topic,
        @NotEmpty(message = "관심사는 최소 3개 이상 선택해야 합니다.")
        @Size(min = 3, max = 5, message = "관심사는 3~5개까지 선택 가능합니다.")
        List<InterestType> interests
) {
}
