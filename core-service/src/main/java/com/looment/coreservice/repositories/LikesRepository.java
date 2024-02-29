package com.looment.coreservice.repositories;

import com.looment.coreservice.dtos.responses.likes.LikeResponse;
import com.looment.coreservice.entities.Likes;
import com.looment.coreservice.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikesRepository extends JpaRepository<Likes, UUID> {
    Optional<Likes> findByParentIdEqualsAndUsers(UUID parentId, Users users);

    @Query("SELECT new com.looment.coreservice.dtos.responses.likes.LikeResponse(" +
            "l.id as id, " +
            "l.users.id as userId, " +
            "l.createdAt as createdAt, " +
            "l.updatedAt as updatedAt, " +
            "l.users.profileUrl as profileUrl," +
            "l.users.username as username," +
            "l.deletedAt as deletedAt )" +
            "from Likes l " +
            "where l.parentId =:parentId AND deletedAt IS NULL")
    List<LikeResponse> findByParentEqualsAndDeletedAtIsNull(UUID parentId);

    Page<Likes> findByParentIdEqualsAndDeletedAtIsNullOrderByUpdatedAtDesc(Pageable pageable, UUID parentId);
    Page<Likes> findByUsers_IdEqualsAndDeletedAtIsNullOrderByUpdatedAtDesc(Pageable pageable, UUID userId);
}
