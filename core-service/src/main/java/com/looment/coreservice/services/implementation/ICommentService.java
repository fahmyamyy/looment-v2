package com.looment.coreservice.services.implementation;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.requests.comments.CommentRequest;
import com.looment.coreservice.dtos.responses.comments.CommentInfoResponse;
import com.looment.coreservice.dtos.responses.comments.CommentPaginationResponse;
import com.looment.coreservice.dtos.responses.comments.CommentResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ICommentService {
    CommentResponse newComment(CommentRequest commentRequest);
    CommentInfoResponse infoComment(Pageable pageable, UUID commentId);
    CommentPaginationResponse getCommentByParent(Pageable pageable, UUID parentId);
    Pair<List<CommentResponse>, Pagination> getCommentByUser(Pageable pageable, UUID userId);
    void deleteComment(UUID commentId);
}
