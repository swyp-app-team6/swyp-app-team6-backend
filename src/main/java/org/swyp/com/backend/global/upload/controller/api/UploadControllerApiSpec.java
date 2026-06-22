package org.swyp.com.backend.global.upload.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.swyp.com.backend.global.upload.dto.PresignedUploadResponse;

@Tag(name = "Upload", description = "파일 업로드 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface UploadControllerApiSpec {

    @Operation(
            summary = "S3 Presigned URL 발급",
            description = "파일 업로드를 위한 S3 Presigned URL을 발급합니다. 발급된 URL은 10분간 유효하며, PUT 방식으로 파일을 직접 업로드할 수 있습니다."
                    + "또한, 응답값으로 포함된 imageKey 값을 프로필 생성 API에 사용합니다.",
            operationId = "presignUpload"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "uploadUrl": "https://bucket.s3.amazonaws.com/profile/user-uuid?X-Amz-Signature=...",
                                              "imageKey": "profile/user-uuid"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "UNAUTHORIZED",
                                              "status": 401,
                                              "detail": "인증 정보가 유효하지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<PresignedUploadResponse> presign(
            UserDetails userDetails,
            @Parameter(
                    description = "업로드할 파일의 Content-Type",
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"image/jpeg", "image/png", "image/webp", "image/gif"},
                            defaultValue = "image/jpeg"
                    )
            )
            String contentType
    );
}
