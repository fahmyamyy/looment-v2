package com.looment.coreservice.services.implementation;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.requests.posts.PostRequest;
import com.looment.coreservice.dtos.requests.posts.PostUpdateRequest;
import com.looment.coreservice.dtos.responses.posts.PostInfoResponse;
import com.looment.coreservice.dtos.responses.posts.PostResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPostService {
    PostResponse newPost(PostRequest postRequest);
    PostResponse updatePost(UUID postId, PostUpdateRequest postUpdateRequest);
    Pair<List<PostResponse>, Pagination> getAllPostDefault(Pageable pageable, UUID userId);
//    Pair<List<PostResponse>, Pagination> getAllPostByCategory(Pageable pageable, String category);
    Pair<List<PostResponse>, Pagination> getAllPostByUser(Pageable pageable, UUID userId);
    PostInfoResponse infoPost(Pageable pageable, UUID postId);
    void deletePost(UUID postId);
}
