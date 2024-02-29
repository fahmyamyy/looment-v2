package com.looment.coreservice.repositories;

import com.looment.coreservice.entities.Posts;
import com.looment.coreservice.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostsRepository extends JpaRepository<Posts, UUID> {
    Optional<Posts> findByIdAndUsersAndDeletedAtIsNull(UUID postId, Users users);
    Optional<Posts> findByIdAndDeletedAtIsNull(UUID postId);
    Page<Posts> findPostsByDeletedAtIsNullOrderByTotalLikesDescTotalCommentsDescTotalViewsDescCreatedAtDesc(Pageable pageable);
//    Page<Posts> findPostsByDeletedAtIsNullAndCategoryEqualsIgnoreCaseOrderByTotalLikesDescTotalCommentsDescTotalViewsDescCreatedAtDesc(Pageable pageable, String category);
    Page<Posts> findPostsByUsers_IdEqualsAndDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable, UUID userId);
    Page<Posts> findPostByDeletedAtIsNullAndUsers_IdIsInOrderByCreatedAt(Pageable pageable, List<UUID> userId);
}
