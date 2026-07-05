package org.swyp.com.backend.exchange.domain.repository;

import java.util.List;
import java.util.Optional;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.exchange.dto.ExchangeSortDirection;
import org.swyp.com.backend.exchange.dto.cursor.ExchangeCursor;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.RegionDetail;

public interface ProfileExchangeRepository {

    ProfileExchange save(ProfileExchange profileExchange);

    List<ProfileExchange> searchArchive(Long userId, String keyword, List<RegionDetail> regions,
                                        List<CosmicDatingType> types, ExchangeSortDirection direction,
                                        ExchangeCursor cursor, int limit);

    long countArchive(Long userId, String keyword, List<RegionDetail> regions, List<CosmicDatingType> types);

    Optional<ProfileExchange> findByIdAndUserId(Long id, Long userId);

    List<ProfileExchange> findAllByIdInAndUserId(List<Long> ids, Long userId);

    void deleteAll(List<ProfileExchange> profileExchanges);
}
