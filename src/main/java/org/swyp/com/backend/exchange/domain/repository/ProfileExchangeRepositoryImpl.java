package org.swyp.com.backend.exchange.domain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.exchange.dto.ExchangeSortDirection;
import org.swyp.com.backend.exchange.dto.cursor.ExchangeCursor;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.RegionDetail;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.user.domain.User;

@Repository
@RequiredArgsConstructor
public class ProfileExchangeRepositoryImpl implements ProfileExchangeRepository {

    private static final String BLOCK_EXCLUSION_CLAUSE =
            " AND NOT EXISTS (SELECT 1 FROM Block b "
                    + "WHERE b.blockerUser.id = p.user.id AND b.blockedUser.id = :userId)";

    private final ProfileExchangeJpaRepository profileExchangeJpaRepository;
    private final EntityManager entityManager;

    @Override
    public ProfileExchange save(ProfileExchange profileExchange) {
        return profileExchangeJpaRepository.save(profileExchange);
    }

    @Override
    public List<ProfileExchange> searchArchive(Long userId, String keyword, List<RegionDetail> regions,
                                               List<CosmicDatingType> types, Boolean liked,
                                               ExchangeSortDirection direction,
                                               ExchangeCursor cursor, int limit) {
        boolean recent = direction != ExchangeSortDirection.OLDEST;
        String operator = recent ? "<" : ">";
        String order = recent ? "DESC" : "ASC";

        StringBuilder jpql = new StringBuilder(
                "SELECT pe FROM ProfileExchange pe "
                        + "LEFT JOIN FETCH pe.profile p "
                        + "LEFT JOIN FETCH p.cosmic c "
                        + "JOIN FETCH pe.exchange ex "
                        + "WHERE pe.user.id = :userId"
                        + BLOCK_EXCLUSION_CLAUSE);

        FilterClause filterClause = buildFilterClause(keyword, regions, types, liked);
        jpql.append(filterClause.jpql());

        if (cursor != null) {
            jpql.append(" AND (ex.createdAt ").append(operator).append(" :cursorCreatedAt")
                    .append(" OR (ex.createdAt = :cursorCreatedAt AND pe.id ").append(operator)
                    .append(" :cursorId))");
        }

        jpql.append(" ORDER BY ex.createdAt ").append(order).append(", pe.id ").append(order);

        TypedQuery<ProfileExchange> query = entityManager.createQuery(jpql.toString(), ProfileExchange.class);
        query.setParameter("userId", userId);
        filterClause.parameters().forEach(query::setParameter);
        if (cursor != null) {
            query.setParameter("cursorCreatedAt", cursor.createdAt());
            query.setParameter("cursorId", cursor.profileExchangeId());
        }
        query.setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public long countArchive(Long userId, String keyword, List<RegionDetail> regions, List<CosmicDatingType> types,
                             Boolean liked) {
        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(pe) FROM ProfileExchange pe "
                        + "LEFT JOIN pe.profile p "
                        + "LEFT JOIN p.cosmic c "
                        + "WHERE pe.user.id = :userId"
                        + BLOCK_EXCLUSION_CLAUSE);

        FilterClause filterClause = buildFilterClause(keyword, regions, types, liked);
        jpql.append(filterClause.jpql());

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        query.setParameter("userId", userId);
        filterClause.parameters().forEach(query::setParameter);

        return query.getSingleResult();
    }

    @Override
    public Optional<ProfileExchange> findByIdAndUserId(Long id, Long userId) {
        try {
            ProfileExchange result = entityManager.createQuery(
                            "SELECT pe FROM ProfileExchange pe "
                                    + "LEFT JOIN FETCH pe.profile p "
                                    + "LEFT JOIN FETCH p.cosmic c "
                                    + "JOIN FETCH pe.exchange ex "
                                    + "WHERE pe.id = :id AND pe.user.id = :userId",
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

    @Override
    public Optional<ProfileExchange> findById(Long id) {
        return profileExchangeJpaRepository.findById(id);
    }

    @Override
    public void deleteByUser(User user) {
        profileExchangeJpaRepository.deleteByUser(user);
    }

    @Override
    public List<ProfileExchange> findByProfile(Profile profile) {
        return profileExchangeJpaRepository.findByProfile(profile);
    }

    /**
     * JPQL 조건절과 바인딩할 파라미터를 한 곳에서 함께 만든다. 조건 추가 여부와 파라미터 바인딩 여부가 서로 다른 메서드에서 따로 판단되면 어긋날 수 있어 하나로 묶는다.
     */
    private FilterClause buildFilterClause(String keyword, List<RegionDetail> regions, List<CosmicDatingType> types,
                                           Boolean liked) {
        StringBuilder jpql = new StringBuilder();
        Map<String, Object> parameters = new LinkedHashMap<>();

        if (keyword != null && !keyword.isBlank()) {
            jpql.append(" AND p.nickname LIKE CONCAT('%', :keyword, '%')");
            parameters.put("keyword", keyword);
        }
        if (regions != null && !regions.isEmpty()) {
            jpql.append(" AND p.regionDetail IN :regions");
            parameters.put("regions", regions);
        }
        if (types != null && !types.isEmpty()) {
            jpql.append(" AND c.type IN :types");
            parameters.put("types", types);
        }
        if (liked != null) {
            jpql.append(" AND pe.liked = :liked");
            parameters.put("liked", liked);
        }

        return new FilterClause(jpql.toString(), parameters);
    }

    private record FilterClause(String jpql, Map<String, Object> parameters) {

    }
}
