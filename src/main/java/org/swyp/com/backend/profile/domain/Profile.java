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
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.swyp.com.backend.cosmic.domain.Cosmic;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.RegionDetail;
import org.swyp.com.backend.user.domain.User;

@Entity
@Getter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @Column(nullable = false, length = 10)
    private String nickname;
    @Column(nullable = false, name = "image_key")
    private String imageKey;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(nullable = false)
    private Integer age;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RegionDetail regionDetail;
    @Column(nullable = false, length = 20)
    private String job;

    @Column(length = 20)
    private String bio;
    @ManyToOne
    @JoinColumn(name = "cosmic_id")
    private Cosmic cosmic;

    @Column(length = 36)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID qr;
    @Column(name = "qr_expires_at")
    private Date qrExpiresAt;

    @Column(nullable = false)
    private Boolean deleted;

    public static Profile createProfile(User user, String nickname, String imageKey, Gender gender, Integer age,
                                        RegionDetail regionDetail, String job,
                                        String bio, Cosmic cosmic) {
        Profile profile = new Profile();
        profile.user = user;
        profile.nickname = nickname;
        profile.imageKey = imageKey;
        profile.gender = gender;
        profile.age = age;
        profile.regionDetail = regionDetail;
        profile.job = job;
        profile.bio = bio;
        profile.cosmic = cosmic;
        profile.deleted = false;
        return profile;
    }

    public void updateProfile(String nickname, String imageKey, Integer age, RegionDetail regionDetail, String job,
                              String bio,
                              Cosmic cosmic) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (imageKey != null) {
            this.imageKey = imageKey;
        }
        if (age != null) {
            this.age = age;
        }
        if (regionDetail != null) {
            this.regionDetail = regionDetail;
        }
        if (job != null) {
            this.job = job;
        }
        if (bio != null) {
            this.bio = bio;
        }
        if (cosmic != null) {
            this.cosmic = cosmic;
        }
    }

    public void deleteProfile() {
        this.deleted = true;
    }

    public void updateProfileQR(UUID qr, Date qrExpiresAt) {
        this.qr = qr;
        this.qrExpiresAt = qrExpiresAt;
    }
}
