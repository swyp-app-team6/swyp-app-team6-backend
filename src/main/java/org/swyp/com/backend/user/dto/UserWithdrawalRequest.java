package org.swyp.com.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.swyp.com.backend.global.enumeration.WithdrawalReasonCode;

@Schema(description = "회원 탈퇴 요청")
public record UserWithdrawalRequest(
        @NotNull(message = "탈퇴 사유를 선택해주세요.")
        @Schema(description = "탈퇴 사유 코드")
        WithdrawalReasonCode reasonCode,

        @Size(max = 300, message = "상세 사유는 300자를 초과할 수 없습니다.")
        @Schema(description = "기타 사유 선택 시 입력하는 상세 사유 (최대 300자)")
        String reasonDetail
) {

}
