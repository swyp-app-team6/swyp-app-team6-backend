package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

public record ProfileCosmicUpdateRequest(
        @JsonProperty("cosmic_type")
        CosmicDatingType cosmicType
) {
}
