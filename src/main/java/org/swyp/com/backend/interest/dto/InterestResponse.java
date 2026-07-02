package org.swyp.com.backend.interest.dto;

import java.util.List;

public record InterestResponse(
        List<InterestTypeLabel> interests
) {
}
