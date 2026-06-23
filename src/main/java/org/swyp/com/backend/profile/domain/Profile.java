package org.swyp.com.backend.profile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.user.domain.User;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, length = 10)
    private String nickname;
    @Column(name = "image_key")
    private String imageKey;
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
    // 질문템플릿
    // 연애유형

    public static Profile createProfile(User user, ProfileRegisterRequest request) {
        Profile profile = new Profile();
        profile.user = user;
        profile.nickname = request.nickname();
        profile.imageKey = request.imageKey();
        profile.gender = request.gender();
        profile.bio = request.bio();
        profile.keyword = request.keyword();
        profile.topic = request.topic();
        return profile;
    }
}
