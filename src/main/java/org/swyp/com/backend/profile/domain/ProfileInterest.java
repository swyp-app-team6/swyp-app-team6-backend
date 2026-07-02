package org.swyp.com.backend.profile.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import org.swyp.com.backend.interest.domain.Interest;

@Entity
@Getter
public class ProfileInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Profile profile;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Interest interest;

    public static ProfileInterest createProfileInterest(Profile profile, Interest interest) {
        ProfileInterest profileInterest = new ProfileInterest();
        profileInterest.profile = profile;
        profileInterest.interest = interest;
        return profileInterest;
    }
}
