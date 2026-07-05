package org.swyp.com.backend.exchange.domain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.exchange.dto.ExchangeSortDirection;
import org.swyp.com.backend.exchange.dto.cursor.ExchangeCursor;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.RegionDetail;

@Repository
@RequiredArgsConstructor
public class ProfileExchangeRepositoryImpl implements ProfileExchangeRepository {

    private final ProfileExchangeJpaRepository profileExchangeJpaRepository;
    private final EntityManager entityManager;

    @Override
    public ProfileExchange save(ProfileExchange profileExchange) {
        return profileExchangeJpaRepository.save(profileExchange);
    }

    @Override
    public List<ProfileExchange> searchArchive(Long userId, String keyword, List<RegionDetail> regions,
                                                List<CosmicDatingType> types, ExchangeSortDirection direction,
                                                ExchangeCursor cursor, int limit) {
        boolean recent = direction != ExchangeSortDirection.OLDEST;
        String operator = recent ? "<" : ">";
        String order = recent ? "DESC" : "ASC";

        StringBuilder jpql = new StringBuilder(
                "SELECT pe FROM ProfileExchange pe "
                        + "JOIN FETCH pe.profile p "
                        + "LEFT JOIN FETCH p.cosmic c "
                        + "JOIN FETCH pe.exchange ex "
                        + "WHERE pe.user.id = :userId AND p.deleted = false");

        appendFilters(jpql, keyword, regions, types);

        if (cursor != null) {
            jpql.append(" AND (ex.createdAt ").append(operator).append(" :cursorCreatedAt")
                    .append(" OR (ex.createdAt = :cursorCreatedAt AND pe.id ").append(operator)
                    .append(" :cursorId))");
        }

        jpql.append(" ORDER BY ex.createdAt ").append(order).append(", pe.id ").append(order);

        TypedQuery<ProfileExchange> query = entityManager.createQuery(jpql.toString(), ProfileExchange.class);
        query.setParameter("userId", userId);
        bindFilters(query, keyword, regions, types);
        if (cursor != null) {
            query.setParameter("cursorCreatedAt", cursor.createdAt());
            query.setParameter("cursorId", cursor.profileExchangeId());
        }
        query.setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public long countArchive(Long userId, String keyword, List<RegionDetail> regions, List<CosmicDatingType> types) {
        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(pe) FROM ProfileExchange pe "
                        + "JOIN pe.profile p "
                        + "LEFT JOIN p.cosmic c "
                        + "WHERE pe.user.id = :userId AND p.deleted = false");

        appendFilters(jpql, keyword, regions, types);

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        query.setParameter("userId", userId);
        bindFilters(query, keyword, regions, types);

        return query.getSingleResult();
    }

    @Override
    public Optional<ProfileExchange> findByIdAndUserId(Long id, Long userId) {
        try {
            ProfileExchange result = entityManager.createQuery(
                            "SELECT pe FROM ProfileExchange pe "
                                    + "JOIN FETCH pe.profile p "
                                    + "LEFT JOIN FETCH p.cosmic c "
                                    + "JOIN FETCH pe.exchange ex "
                                    + "WHERE pe.id = :id AND pe.user.id = :userId AND p.deleted = false",
                            ProfileExchange.class)
                    .setParameter("id", id)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ProfileExchange> findAllByIdInAndUserId(List<Long> ids, Long userId) {
        return entityManager.createQuery(
                        "SELECT pe FROM ProfileExchange pe WHERE pe.id IN :ids AND pe.user.id = :userId",
                        ProfileExchange.class)
                .setParameter("ids", ids)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public void deleteAll(List<ProfileExchange> profileExchanges) {
        profileExchangeJpaRepository.deleteAllInBatch(profileExchanges);
    }

    private void appendFilters(StringBuilder jpql, String keyword, List<RegionDetail> regions,
                                List<CosmicDatingType> types) {
        if (keyword != null && !keyword.isBlank()) {
            jpql.append(" AND p.nickname LIKE CONCAT('%', :keyword, '%')");
        }
        if (regions != null && !regions.isEmpty()) {
            jpql.append(" AND p.regionDetail IN :regions");
        }
        if (types != null && !types.isEmpty()) {
            jpql.append(" AND c.type IN :types");
        }
    }

    private void bindFilters(TypedQuery<?> query, String keyword, List<RegionDetail> regions,
                              List<CosmicDatingType> types) {
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", keyword);
        }
        if (regions != null && !regions.isEmpty()) {
            query.setParameter("regions", regions);
        }
        if (types != null && !types.isEmpty()) {
            query.setParameter("types", types);
        }
    }
}
