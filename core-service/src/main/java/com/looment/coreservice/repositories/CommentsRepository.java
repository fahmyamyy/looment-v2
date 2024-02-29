package com.looment.coreservice.repositories;

import com.looment.coreservice.entities.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentsRepository extends JpaRepository<Comments, UUID> {
    Optional<Comments> findByIdAndDeletedAtIsNull(UUID parentId);
    Page<Comments> findByParentIdEquals(Pageable pageable, UUID parentId);
    Page<Comments> findByParentIdEqualsAndDeletedAtIsNullOrderByTotalLikesDescTotalCommentsDescCreatedAtAsc(Pageable pageable, UUID parentId);
    Page<Comments> findByUsers_IdEqualsAndDeletedAtIsNullOrderByCreatedAtAsc(Pageable pageable, UUID userId);
}
