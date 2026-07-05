package org.swyp.com.backend.support;

import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.exchange.domain.Exchange;
import org.swyp.com.backend.exchange.domain.MatchedInterest;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.user.domain.User;

public class ExchangeTestFixture {

    public static Exchange createExchange(Long id, LocalDateTime createdAt) {
        Exchange exchange = new Exchange();
        ReflectionTestUtils.setField(exchange, "id", id);
        ReflectionTestUtils.setField(exchange, "createdAt", createdAt);
        return exchange;
    }

    public static ProfileExchange createProfileExchange(Long id, User user, Profile profile, Exchange exchange,
                                                         String memo, Integer score) {
        ProfileExchange profileExchange = new ProfileExchange();
        ReflectionTestUtils.setField(profileExchange, "id", id);
        ReflectionTestUtils.setField(profileExchange, "user", user);
        ReflectionTestUtils.setField(profileExchange, "profile", profile);
        ReflectionTestUtils.setField(profileExchange, "exchange", exchange);
        ReflectionTestUtils.setField(profileExchange, "memo", memo);
        ReflectionTestUtils.setField(profileExchange, "score", score);
        return profileExchange;
    }

    public static MatchedInterest createMatchedInterest(Long id, Exchange exchange, InterestType type) {
        MatchedInterest matchedInterest = new MatchedInterest();
        ReflectionTestUtils.setField(matchedInterest, "id", id);
        ReflectionTestUtils.setField(matchedInterest, "exchange", exchange);
        ReflectionTestUtils.setField(matchedInterest, "type", type);
        return matchedInterest;
    }
}
