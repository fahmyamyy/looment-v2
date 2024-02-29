package com.looment.coreservice.repositories;

import com.looment.coreservice.entities.Follows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowsRepository extends JpaRepository<Follows, UUID> {
    Optional<Follows> findByFollowed_IdEqualsAndFollower_IdEquals(UUID followedId, UUID followersId);

    @Query(value = "SELECT * FROM users_follow " +
            "WHERE followed_id = :userId " +
            "AND deleted_at IS NULL " +
            "ORDER BY updated_at DESC", nativeQuery = true)
    Page<Follows> findFollowers(Pageable pageable, @Param("userId") UUID userId);

    @Query(value = "SELECT * FROM users_follow " +
            "WHERE follower_id = :userId " +
            "AND deleted_at IS NULL " +
            "ORDER BY updated_at DESC", nativeQuery = true)
    Page<Follows> findFollowings(Pageable pageable, @Param("userId") UUID userId);

    @Query(value = "SELECT followed_id FROM users_follow " +
            "WHERE follower_id = :userId " +
            "AND deleted_at IS NULL " +
            "ORDER BY updated_at DESC", nativeQuery = true)
    List<UUID> findFollowings(@Param("userId") UUID userId);
}
