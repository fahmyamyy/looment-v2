package com.looment.coreservice.services.implementation;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.requests.follows.FollowRequest;
import com.looment.coreservice.dtos.responses.follows.FollowResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IFollowService {
    Pair<List<FollowResponse>, Pagination> getFollowers(Pageable pageable, UUID userId);
    Pair<List<FollowResponse>, Pagination> getFollowings(Pageable pageable, UUID userId);
    void toggleFollow(FollowRequest followRequest);
    List<UUID> getFollowingsId(UUID userId);
}
