package com.looment.coreservice.services;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.UploadRequest;
import com.looment.coreservice.dtos.UploadResponse;
import com.looment.coreservice.dtos.requests.posts.PostRequest;
import com.looment.coreservice.dtos.requests.posts.PostUpdateRequest;
import com.looment.coreservice.dtos.responses.comments.CommentPaginationResponse;
import com.looment.coreservice.dtos.responses.posts.PostInfoResponse;
import com.looment.coreservice.dtos.responses.posts.PostResponse;
import com.looment.coreservice.entities.Posts;
import com.looment.coreservice.entities.Users;
import com.looment.coreservice.exceptions.FailedRestCall;
import com.looment.coreservice.exceptions.users.UserNotExists;
import com.looment.coreservice.exceptions.posts.PostNotExists;
import com.looment.coreservice.repositories.PostsRepository;
import com.looment.coreservice.repositories.UsersRepository;
import com.looment.coreservice.services.implementation.IPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"post"})
public class PostService implements IPostService {
    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;
    private final FollowService followService;
    private final RestCallService restCallService;
    private final CommentService commentService;
    private final UploadService uploadService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final Environment environment;

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "post", allEntries = true),
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "user_posts", allEntries = true)
    })
    public PostResponse newPost(PostRequest postRequest) {
        Users users = usersRepository.findById((postRequest.getUsers()))
                .orElseThrow(UserNotExists::new);

        Posts posts = new Posts();
        posts.setCaption(postRequest.getCaption());
        posts.setLocation(postRequest.getLocation());
        posts.setUsers(users);

        List<MultipartFile> files = postRequest.getFiles();
        List<String> urls = null;
        if (!files.get(0).isEmpty()) {
            urls = new ArrayList<>();
            for (MultipartFile file : files) {
                UploadRequest uploadRequest = new UploadRequest(postRequest.getUsers().toString(), posts.getId().toString(), file);

                UploadResponse responseUpload = restCallService.callApiUpload(uploadRequest);
                urls.add(responseUpload.getUrl());
            }
        }
        postsRepository.save(posts);

        PostResponse postResponse = modelMapper.map(posts, PostResponse.class);
        postResponse.setUrl(urls);
        return postResponse;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "post", allEntries = true),
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "user_posts", allEntries = true)
    })
    public PostResponse updatePost(UUID postId, PostUpdateRequest postUpdateRequest) {
        Users users = usersRepository.findById(postUpdateRequest.getUserId())
                .orElseThrow(UserNotExists::new);

        Posts posts = postsRepository.findByIdAndUsersAndDeletedAtIsNull(postId, users)
                .orElseThrow(PostNotExists::new);

        posts.setCaption(postUpdateRequest.getCaption());
        posts.setLocation(postUpdateRequest.getLocation());
        posts.setCommentable(postUpdateRequest.getCommentable());
        postsRepository.save(posts);
        return modelMapper.map(posts, PostResponse.class);
    }

    @Override
    @Cacheable(value = "posts")
    public Pair<List<PostResponse>, Pagination> getAllPostDefault(Pageable pageable, UUID userId) {
        List<UUID> usersList = followService.getFollowingsId(userId);
        Page<Posts> followingPost = postsRepository.findPostByDeletedAtIsNullAndUsers_IdIsInOrderByCreatedAt(pageable, usersList);
        Page<Posts> popularPost = postsRepository.findPostsByDeletedAtIsNullOrderByTotalLikesDescTotalCommentsDescTotalViewsDescCreatedAtDesc(pageable);

        List<Posts> postPage = new ArrayList<>();
        postPage.addAll(popularPost.getContent());
        postPage.addAll(followingPost.getContent());
        postPage.sort(Comparator.comparing(Posts::getUpdatedAt));

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), postPage.size());
        Page<Posts> sortedPostPage = new PageImpl<>(postPage.subList(start, end), pageable, postPage.size());

        return convertToPagination(sortedPostPage);
    }

    @Override
    @Cacheable(value = "user_posts")
    public Pair<List<PostResponse>, Pagination> getAllPostByUser(Pageable pageable, UUID userId) {
        Users users = usersRepository.findById(userId)
                .orElseThrow(UserNotExists::new);

        Page<Posts> postPage = postsRepository.findPostsByUsers_IdEqualsAndDeletedAtIsNullOrderByCreatedAtDesc(pageable, users.getId());
        return convertToPagination(postPage);
    }

    @Override
    @Cacheable(value = "post")
    public PostInfoResponse infoPost(Pageable pageable, UUID postId) {
        Posts posts = postsRepository.findById(postId)
                .orElseThrow(PostNotExists::new);

        CommentPaginationResponse comments = new CommentPaginationResponse<>();
        if (posts.getTotalComments() > 0) {
            comments = commentService.getCommentByParent(pageable, postId);
        }

        List<String> urls = uploadService.getUrlParent(postId);

        PostInfoResponse postResponse = modelMapper.map(posts, PostInfoResponse.class);
        postResponse.setUrl(urls);
        postResponse.setCommentList(comments);
        addTotalViews(List.of(posts));
        return postResponse;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "post", allEntries = true),
            @CacheEvict(value = "posts", allEntries = true)
    })
    public void deletePost(UUID postId) {
        LocalDateTime now = LocalDateTime.now();

        Posts posts = postsRepository.findById(postId)
                .orElseThrow(PostNotExists::new);

        String responseUpload = restCallService.deleteToHost(environment.getProperty("port.upload") + "/" + postId);
        if (!("Successfully delete Attachments".equalsIgnoreCase(responseUpload))) {
            throw new FailedRestCall();
        }

        uploadService.deleteByParent(postId, now);
        posts.setDeletedAt(now);
        postsRepository.save(posts);
    }

    private Pair<List<PostResponse>, Pagination> convertToPagination(Page<Posts> postPage) {
        List<PostResponse> postResponseList = postPage.stream()
                .map(post -> modelMapper.map(post, PostResponse.class))
                .toList();
        Pagination pagination = new Pagination(
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                postPage.getNumber() + 1
        );
        addTotalViews(postPage.getContent());
        return new ImmutablePair<>(postResponseList, pagination);
    }

    private void addTotalViews(List<Posts> posts) {
        posts.forEach(post -> post.setTotalViews(post.getTotalViews() + 1));
        postsRepository.saveAll(posts);
    }

//    Futher development: get all post by tags
//    @Override
//    @Cacheable(value = "posts")
//    public Pair<List<PostResponse>, Pagination> getAllPostByCategory(Pageable pageable, String category) {
//        if (Boolean.FALSE.equals(getPostCategory(category))) {
//            throw new PostInvalidCategory();
//        }
//        Page<Posts> postPage = postsRepository.findPostsByDeletedAtIsNullAndCategoryEqualsIgnoreCaseOrderByTotalLikesDescTotalCommentsDescTotalViewsDescCreatedAtDesc(pageable, category);
//        return convertToPagination(postPage);
//    }

//    private Boolean getPostCategory(String query) {
//        for (PostCategory category : PostCategory.values()) {
//            if (category.name().equalsIgnoreCase(query)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
