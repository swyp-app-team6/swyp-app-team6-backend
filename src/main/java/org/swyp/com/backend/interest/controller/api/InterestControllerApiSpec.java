package org.swyp.com.backend.interest.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Interest", description = "관심사 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface InterestControllerApiSpec {
}
