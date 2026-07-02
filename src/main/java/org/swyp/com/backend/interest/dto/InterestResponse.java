package org.swyp.com.backend.interest.dto;

import java.util.List;
import org.swyp.com.backend.profile.dto.InterestTypeLabel;

public record InterestResponse(
        List<InterestTypeLabel> interests
) {
}
