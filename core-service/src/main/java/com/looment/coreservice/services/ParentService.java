package com.looment.coreservice.services;

import com.looment.coreservice.dtos.ParentDto;
import com.looment.coreservice.entities.Comments;
import com.looment.coreservice.entities.Posts;
import com.looment.coreservice.exceptions.comments.CommentNotExists;
import com.looment.coreservice.exceptions.posts.PostNotExists;
import com.looment.coreservice.repositories.CommentsRepository;
import com.looment.coreservice.repositories.PostsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParentService {
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;

    public ParentDto valid(UUID parentId) {
        if (postsRepository.findByIdAndDeletedAtIsNull(parentId).isPresent()) {
            return new ParentDto("Posts", true);
        } else if (commentsRepository.findByIdAndDeletedAtIsNull(parentId).isPresent()) {
            return new ParentDto("Comments", true);
        } else {
            return new ParentDto(null, false);
        }
    }

    public void updateTotalLikes(ParentDto parentDto, UUID parentId, String operation) {

        switch (parentDto.getParentType()) {
            case "Posts":
                Posts posts = postsRepository.findById(parentId).orElseThrow(PostNotExists::new);
                posts.setTotalLikes(setTotalLikes(posts.getTotalLikes(), operation));
                postsRepository.save(posts);
                break;
            case "Comments":
                Comments comments = commentsRepository.findById(parentId).orElseThrow(CommentNotExists::new);
                comments.setTotalLikes(setTotalLikes(comments.getTotalLikes(), operation));
                commentsRepository.save(comments);
                break;
            default:
        }
    }

    public void updateTotalComments(ParentDto parentDto, UUID parentId, String operation) {
        switch (parentDto.getParentType()) {
            case "Posts":
                Posts posts = postsRepository.findById(parentId).orElseThrow(PostNotExists::new);
                posts.setTotalComments(setTotalComments(posts.getTotalComments(), operation));
                postsRepository.save(posts);
                break;
            case "Comments":
                Comments comments = commentsRepository.findById(parentId).orElseThrow(CommentNotExists::new);
                comments.setTotalComments(setTotalComments(comments.getTotalComments(), operation));
                commentsRepository.save(comments);
                break;
            default:
        }
    }

    public Integer setTotalLikes(Integer current, String operation) {
        if ("increment".equals(operation)) {
            return current + 1;
        } else {
            return current - 1;
        }
    }

    public Integer setTotalComments(Integer current, String operation) {
        if ("increment".equals(operation)) {
            return current + 1;
        } else {
            return current - 1;
        }
    }

}
