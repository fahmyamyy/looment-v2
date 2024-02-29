package com.looment.coreservice.controllers;

import com.looment.coreservice.dtos.BaseResponse;
import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.PaginationResponse;
import com.looment.coreservice.dtos.requests.comments.CommentRequest;
import com.looment.coreservice.dtos.responses.comments.CommentInfoResponse;
import com.looment.coreservice.dtos.responses.comments.CommentResponse;
import com.looment.coreservice.services.CommentService;
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
@RequestMapping("v1/comments")
@RequiredArgsConstructor
public class CommentController extends BaseController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<BaseResponse> newComment(@ModelAttribute @Valid CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.newComment(commentRequest);
        return responseSuccess("Successfully created a new Comment", commentResponse);
    }

    @GetMapping("{commentId}")
    public ResponseEntity<BaseResponse> getByCommentId(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @PathVariable UUID commentId
    ) {
        int offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        CommentInfoResponse commentResponse = commentService.infoComment(pageable, commentId);
        return responseSuccess("Successfully fetch Comment info", commentResponse);
    }

    @GetMapping("user/{userId}")
    public PaginationResponse<List<CommentResponse>> getCommentByUser(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @PathVariable UUID userId
    ) {
        int offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        Pair<List<CommentResponse>, Pagination> pagination = commentService.getCommentByUser(pageable, userId);
        return responsePagination("Successfully fetch all Comments from User: " + userId, pagination.getLeft(), pagination.getRight());
    }

    @DeleteMapping("{commentId}")
    public ResponseEntity<BaseResponse> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return responseDelete("Successfully deleted a Comment");
    }
}
