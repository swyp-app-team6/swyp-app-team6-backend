package org.swyp.com.backend.exchange.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.user.domain.User;

@Entity
@Getter
public class ProfileExchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @JoinColumn(name = "profile_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Profile profile;
    @JoinColumn(name = "exchange_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Exchange exchange;
    private String memo;
    private Integer score;
    private Boolean liked;

    public static ProfileExchange createProfileExchange(User user, Profile profile, Exchange exchange) {
        ProfileExchange profileExchange = new ProfileExchange();
        profileExchange.user = user;
        profileExchange.profile = profile;
        profileExchange.exchange = exchange;
        profileExchange.liked = false;
        return profileExchange;
    }

    public void updateLiked(boolean liked) {
        this.liked = liked;
    }
}
