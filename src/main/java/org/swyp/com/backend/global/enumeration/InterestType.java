package org.swyp.com.backend.global.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterestType {
    TRAVEL("여행"),
    SPORTS("운동"),
    MUSIC("음악"),
    VIDEO("유튜브"),
    RESTAURANT("맛집"),
    CAFE("카페"),
    CULTURE("문화생활"),
    READING("독서"),
    GAME("게임"),
    SELF_DEVELOPMENT("자기계발"),
    INVESTING("재테크"),
    MOVIE("영화");

    private final String label;
}
