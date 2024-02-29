package com.looment.coreservice.controllers;

import com.looment.coreservice.dtos.BaseResponse;
import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.PaginationResponse;
import com.looment.coreservice.dtos.requests.likes.LikeRequest;
import com.looment.coreservice.dtos.responses.likes.LikeResponse;
import com.looment.coreservice.services.LikeService;
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
@RequestMapping("v1/likes")
@RequiredArgsConstructor
public class LikeController extends BaseController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<BaseResponse> toggleLike(@RequestBody @Valid LikeRequest likeRequest) {
        likeService.toggleLike(likeRequest);
        return responseSuccess("Successfully toggle a Like");
    }

    @GetMapping("{parentId}")
    public PaginationResponse<List<LikeResponse>> getLikesByParent(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @PathVariable UUID parentId
    ) {
        int offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        Pair<List<LikeResponse>, Pagination> pagination = likeService.getLikesByParent(pageable, parentId);
        return responsePagination("Successfully fetch all Likes with Parent ID: " + parentId, pagination.getLeft(), pagination.getRight());
    }

    @GetMapping("user/{userId}")
    public PaginationResponse<List<LikeResponse>> getLikesByUser(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @PathVariable UUID userId
    ) {
        int offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        Pair<List<LikeResponse>, Pagination> pagination = likeService.getLikesByUser(pageable, userId);
        return responsePagination("Successfully fetch all Likes from User: " + userId, pagination.getLeft(), pagination.getRight());
    }
}