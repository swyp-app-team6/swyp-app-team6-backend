package org.swyp.com.backend.global.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CosmicDatingType {
    GALAXY("갤럭시 형"),
    SHOOTING_STAR("슈팅스타 형"),
    LUNA("루나 형"),
    SOLA("솔라 형");

    private final String label;
}
