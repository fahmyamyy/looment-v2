package com.looment.coreservice.services;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.ParentDto;
import com.looment.coreservice.dtos.UploadRequest;
import com.looment.coreservice.dtos.UploadResponse;
import com.looment.coreservice.dtos.requests.comments.CommentRequest;
import com.looment.coreservice.dtos.responses.comments.CommentInfoResponse;
import com.looment.coreservice.dtos.responses.comments.CommentPaginationResponse;
import com.looment.coreservice.dtos.responses.comments.CommentResponse;
import com.looment.coreservice.entities.Comments;
import com.looment.coreservice.entities.Users;
import com.looment.coreservice.exceptions.FailedRestCall;
import com.looment.coreservice.exceptions.ParentNotExists;
import com.looment.coreservice.exceptions.users.UserNotExists;
import com.looment.coreservice.exceptions.comments.CommentNotExists;
import com.looment.coreservice.repositories.CommentsRepository;
import com.looment.coreservice.repositories.UsersRepository;
import com.looment.coreservice.services.implementation.ICommentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"comment"})
public class CommentService implements ICommentService {
    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;
    private final RestCallService restCallService;
    private final ParentService parentService;
    private final UploadService uploadService;
    private final Environment environment;

    @Override
    @Caching( evict = {
            @CacheEvict(value = "comment", allEntries = true),
            @CacheEvict(value = "comments", allEntries = true),
            @CacheEvict(value = "post", allEntries = true),
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "answer", allEntries = true),
            @CacheEvict(value = "answers", allEntries = true),
    })
    public CommentResponse newComment(CommentRequest commentRequest) {
        ParentDto parentDto = parentService.valid(commentRequest.getParent());
        if (Boolean.FALSE.equals(parentDto.getStatus())) {
            throw new ParentNotExists();
        }

        Users users = usersRepository.findById(commentRequest.getUsers())
                .orElseThrow(UserNotExists::new);

        Comments comments = new Comments();
        comments.setParentId(commentRequest.getParent());
        comments.setComment(commentRequest.getComment());
        comments.setUsers(users);

        List<MultipartFile> files = commentRequest.getAttachments();
        List<String> urls = null;
        if (!files.get(0).isEmpty()) {
            urls = new ArrayList<>();
            for (MultipartFile file : files) {
                UploadRequest uploadRequest = new UploadRequest(commentRequest.getUsers().toString(), comments.getId().toString(), file);

                UploadResponse responseUpload = restCallService.callApiUpload(uploadRequest);
                urls.add(responseUpload.getUrl());
            }
            comments.setWithAttachment(true);
        }

        commentsRepository.save(comments);
        parentService.updateTotalComments(parentDto, comments.getParentId(), "increment");

        CommentResponse commentResponse = modelMapper.map(comments, CommentResponse.class);
        commentResponse.setUrl(urls);

        return commentResponse;
    }

    @Override
    @Cacheable(value = "comment")
    public CommentInfoResponse infoComment(Pageable pageable, UUID commentId) {
        Comments commentParent = commentsRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(CommentNotExists::new);

        CommentInfoResponse commentResponse = modelMapper.map(commentParent, CommentInfoResponse.class);

        CommentPaginationResponse comments = new CommentPaginationResponse<>();
        if (commentParent.getTotalComments() > 0) {
            comments = getCommentByParent(pageable, commentId);
        }

        if (Boolean.TRUE.equals(commentParent.getWithAttachment())) {
            List<String> urls = uploadService.getUrlParent(commentId);
            commentResponse.setUrl(urls);
        }
        commentResponse.setCommentList(comments);
        return commentResponse;
    }

    @Override
    public CommentPaginationResponse getCommentByParent(Pageable pageable, UUID parentId) {
        Page<Comments> commentsPage = commentsRepository.findByParentIdEqualsAndDeletedAtIsNullOrderByTotalLikesDescTotalCommentsDescCreatedAtAsc(pageable, parentId);

        List<CommentResponse> commentResponseList = commentsPage.stream()
                .map(c -> {
                    CommentResponse commentResponse = new CommentResponse();
                    commentResponse.setId(c.getId());
                    commentResponse.setParent(c.getParentId());
                    commentResponse.setUsersId(c.getUsers().getId());
                    commentResponse.setComment(c.getComment());
                    commentResponse.setUrl(uploadService.getUrlParent(c.getId()));
                    commentResponse.setTotalComments(c.getTotalComments());
                    commentResponse.setTotalLikes(c.getTotalLikes());
                    return commentResponse;
                }).toList();
        Pagination pagination = new Pagination(
                commentsPage.getTotalPages(),
                commentsPage.getTotalElements(),
                commentsPage.getNumber() + 1
        );
        return new CommentPaginationResponse(commentResponseList, pagination);
    }

    @Override
    public Pair<List<CommentResponse>, Pagination> getCommentByUser(Pageable pageable, UUID userId) {
        Page<Comments> commentsPage = commentsRepository.findByUsers_IdEqualsAndDeletedAtIsNullOrderByCreatedAtAsc(pageable, userId);

        List<CommentResponse> commentResponseList = commentsPage.stream()
                .map(c -> {
                    CommentResponse commentResponse = new CommentResponse();
                    commentResponse.setId(c.getId());
                    commentResponse.setParent(c.getParentId());
                    commentResponse.setUsersId(c.getUsers().getId());
                    commentResponse.setComment(c.getComment());
                    commentResponse.setUrl(uploadService.getUrlParent(c.getId()));
                    commentResponse.setTotalComments(c.getTotalComments());
                    commentResponse.setTotalLikes(c.getTotalLikes());
                    return commentResponse;
                }).toList();
        Pagination pagination = new Pagination(
                commentsPage.getTotalPages(),
                commentsPage.getTotalElements(),
                commentsPage.getNumber() + 1
        );
        return new ImmutablePair<>(commentResponseList, pagination);
    }

    @Override
    @Caching( evict = {
            @CacheEvict(value = "comment", allEntries = true),
            @CacheEvict(value = "comments", allEntries = true)
    })
    public void deleteComment(UUID commentId) {
        Comments comments = commentsRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(CommentNotExists::new);

        if (Boolean.TRUE.equals(comments.getWithAttachment())) {
            String responseUpload = restCallService.deleteToHost(environment.getProperty("port.upload") + "/" + commentId);
            if (!("Successfully delete Attachments".equalsIgnoreCase(responseUpload))) {
                throw new FailedRestCall();
            }
        }

        comments.setDeletedAt(LocalDateTime.now());
        commentsRepository.save(comments);

        parentService.updateTotalComments(new ParentDto("Comments", true), comments.getParentId(), "decrement");
    }

}
