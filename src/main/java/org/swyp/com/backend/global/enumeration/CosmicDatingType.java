package org.swyp.com.backend.global.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CosmicDatingType {
    GALAXY("갤럭시 유형"),
    SHOOTING_STAR("슈팅스타 유형"),
    LUNA("루나 유형"),
    SOLA("솔라 유형");

    private final String label;
}
