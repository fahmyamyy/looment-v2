package com.looment.coreservice.services.implementation;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.requests.likes.LikeRequest;
import com.looment.coreservice.dtos.responses.likes.LikeResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ILikeService {
    void toggleLike(LikeRequest likeRequest);
    Pair<List<LikeResponse>, Pagination> getLikesByParent(Pageable pageable, UUID parentId);
    Pair<List<LikeResponse>, Pagination> getLikesByUser(Pageable pageable, UUID userId);
}
