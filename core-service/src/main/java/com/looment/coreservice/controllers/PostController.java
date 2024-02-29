package com.looment.coreservice.controllers;

import com.looment.coreservice.dtos.BaseResponse;
import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.PaginationResponse;
import com.looment.coreservice.dtos.requests.posts.PostRequest;
import com.looment.coreservice.dtos.requests.posts.PostUpdateRequest;
import com.looment.coreservice.dtos.responses.posts.PostInfoResponse;
import com.looment.coreservice.dtos.responses.posts.PostResponse;
import com.looment.coreservice.services.PostService;
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
@RequestMapping("v1/posts")
@RequiredArgsConstructor
public class PostController extends BaseController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<BaseResponse> newPost(@ModelAttribute @Valid PostRequest postRequest) {
        PostResponse postResponse = postService.newPost(postRequest);
        return responseCreated("Successfully created a new Post", postResponse);
    }

    @PatchMapping("{postId}")
    public ResponseEntity<BaseResponse> updatePost(@RequestBody @Valid PostUpdateRequest postUpdateRequest, @PathVariable UUID postId) {
        PostResponse postResponse = postService.updatePost(postId, postUpdateRequest);
        return responseSuccess("Successfully updated a Post", postResponse);
    }

    @GetMapping("all/{userId}")
    public PaginationResponse<List<PostResponse>> getAllPost(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            @PathVariable UUID userId
    ) {
        int offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        Pair<List<PostResponse>, Pagination> pagination = postService.getAllPostDefault(pageable, userId);
        return responsePagination("Successfully fetch all Post", pagination.getLeft(), pagination.getRight());
    }

    @GetMapping("user/{userId}")
    public PaginationResponse<List<PostResponse>> getAllPostByUser(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @PathVariable UUID userId
    ) {
        int offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        Pair<List<PostResponse>, Pagination> pagination = postService.getAllPostByUser(pageable, userId);
        return responsePagination("Successfully fetch all Post from User: " + userId, pagination.getLeft(), pagination.getRight());
    }

    @GetMapping("{postId}")
    public ResponseEntity<BaseResponse> getByPostId(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @PathVariable UUID postId
    ) {
        int offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        PostInfoResponse postInfoResponse = postService.infoPost(pageable, postId);
        return responseSuccess("Successfully fetch Post info", postInfoResponse);
    }

    @DeleteMapping("{postId}")
    public ResponseEntity<BaseResponse> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return responseDelete("Successfully deleted a Post");
    }

//    @GetMapping("category/{category}")
//    public PaginationResponse<List<PostResponse>> getAllPostByCategory(
//            @RequestParam(name = "page", defaultValue = "1") Integer page,
//            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
//            @PathVariable String category
//    ) {
//        int offset = page - 1;
//        Pageable pageable = PageRequest.of(offset, limit);
//
//        Pair<List<PostResponse>, Pagination> pagination = postService.getAllPostByCategory(pageable, category);
//        return responsePagination("Successfully fetch all Post with category: " + category, pagination.getLeft(), pagination.getRight());
//    }
}
