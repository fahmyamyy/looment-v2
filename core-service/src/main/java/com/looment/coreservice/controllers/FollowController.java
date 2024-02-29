package com.looment.coreservice.controllers;

import com.looment.coreservice.dtos.BaseResponse;
import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.PaginationResponse;
import com.looment.coreservice.dtos.requests.follows.FollowRequest;
import com.looment.coreservice.dtos.responses.follows.FollowResponse;
import com.looment.coreservice.services.FollowService;
import com.looment.coreservice.utils.BaseController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/follows")
@RequiredArgsConstructor
public class FollowController extends BaseController {
    private final FollowService followService;

    @PostMapping
    public ResponseEntity<BaseResponse> toggleFollow(@RequestBody @Valid FollowRequest followRequest) {
        followService.toggleFollow(followRequest);
        return responseSuccess("Successfully Follow User");
    }

    @GetMapping("followers/{userId}")
    public PaginationResponse getFollowers(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @PathVariable UUID userId
    ) {
        Integer offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        Pair<List<FollowResponse>, Pagination> pagination = followService.getFollowers(pageable, userId);
        return responsePagination("Successfully fetch all Users followers", pagination.getLeft(), pagination.getRight());
    }

    @GetMapping("followings/{userId}")
    public PaginationResponse getFollowings(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @PathVariable UUID userId
    ) {
        Integer offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        Pair<List<FollowResponse>, Pagination> pagination = followService.getFollowings(pageable, userId);
        return responsePagination("Successfully fetch all Users followings", pagination.getLeft(), pagination.getRight());
    }


}
