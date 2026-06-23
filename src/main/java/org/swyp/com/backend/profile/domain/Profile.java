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
import jakarta.persistence.OneToOne;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.user.domain.User;

@Entity
@Getter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
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

    public static Profile createProfile(User user, String nickname, String imageKey, Gender gender, String bio,
                                        String keyword, String topic) {
        Profile profile = new Profile();
        profile.user = user;
        profile.nickname = nickname;
        profile.imageKey = imageKey;
        profile.gender = gender;
        profile.bio = bio;
        profile.keyword = keyword;
        profile.topic = topic;
        return profile;
    }

    public void updateProfile(String nickname, String imageKey, String bio,
                              String keyword, String topic) {
        this.nickname = nickname;
        this.imageKey = imageKey;
        this.bio = bio;
        this.keyword = keyword;
        this.topic = topic;
    }
}
