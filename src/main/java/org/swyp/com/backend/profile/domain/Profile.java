package org.swyp.com.backend.profile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
import java.util.UUID;
import org.swyp.com.backend.global.enumeration.Gender;

@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 10)
    private String nickname;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    @Column(nullable = false, length = 20)
    private String bio;
    @Column(nullable = false, length = 10)
    private String keyword;
    @Column(nullable = false, length = 20)
    private String topic;
    @Column(length = 36)
    private UUID qr;
    @Column(name = "qr_expires_at")
    private Date qrExpiresAt;
    // 이미지
    // 관심사
    // 질문템플릿
    // 연애유형
}
