package com.looment.coreservice.services;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.ParentDto;
import com.looment.coreservice.dtos.requests.likes.LikeRequest;
import com.looment.coreservice.dtos.responses.likes.LikeResponse;
import com.looment.coreservice.entities.Likes;
import com.looment.coreservice.entities.Users;
import com.looment.coreservice.exceptions.ParentNotExists;
import com.looment.coreservice.exceptions.users.UserNotExists;
import com.looment.coreservice.repositories.LikesRepository;
import com.looment.coreservice.repositories.UsersRepository;
import com.looment.coreservice.services.implementation.ILikeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"like"})
public class LikeService implements ILikeService {
    private final LikesRepository likesRepository;
    private final UsersRepository usersRepository;
    private final ParentService parentService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    @Caching( evict = {
            @CacheEvict(value = "like", allEntries = true),
            @CacheEvict(value = "likes", allEntries = true),
            @CacheEvict(value = "post", allEntries = true),
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "bounty", allEntries = true),
            @CacheEvict(value = "bounties", allEntries = true),
            @CacheEvict(value = "answer", allEntries = true),
            @CacheEvict(value = "answers", allEntries = true),
            @CacheEvict(value = "comment", allEntries = true),
            @CacheEvict(value = "comments", allEntries = true)
    })
    public void toggleLike(LikeRequest likeRequest) {
        ParentDto parentDto = parentService.valid(likeRequest.getParent());
        if (Boolean.FALSE.equals(parentDto.getStatus())) {
            throw new ParentNotExists();
        }

        Users users = usersRepository.findById(likeRequest.getUsers())
                .orElseThrow(UserNotExists::new);

        Likes likes = likesRepository.findByParentIdEqualsAndUsers(likeRequest.getParent(), users)
                .orElse(null);

        if (likes == null) {
            Likes newLikes = new Likes();
            newLikes.setParentId(likeRequest.getParent());
            newLikes.setUsers(users);
            newLikes.setDeletedAt(null);
            likes = newLikes;
        } else {
            likes.setDeletedAt(likes.getDeletedAt() == null ? LocalDateTime.now() : null);
        }
        likesRepository.save(likes);

        if (likes.getDeletedAt() == null) {
            parentService.updateTotalLikes(parentDto, likeRequest.getParent(), "increment");
        } else {
            parentService.updateTotalLikes(parentDto, likeRequest.getParent(), "decrement");
        }
    }

    @Override
    public Pair<List<LikeResponse>, Pagination> getLikesByParent(Pageable pageable, UUID parentId) {
        Page<Likes> likesPage = likesRepository.findByParentIdEqualsAndDeletedAtIsNullOrderByUpdatedAtDesc(pageable, parentId);

        List<LikeResponse> likeResponseList = likesPage.stream()
                .map(l -> modelMapper.map(l, LikeResponse.class))
                .toList();
        Pagination pagination = new Pagination(
                likesPage.getTotalPages(),
                likesPage.getTotalElements(),
                likesPage.getNumber() + 1
        );
        return new ImmutablePair<>(likeResponseList, pagination);
    }

    @Override
    public Pair<List<LikeResponse>, Pagination> getLikesByUser(Pageable pageable, UUID userId) {
        Page<Likes> likesPage = likesRepository.findByUsers_IdEqualsAndDeletedAtIsNullOrderByUpdatedAtDesc(pageable, userId);

        List<LikeResponse> likeResponseList = likesPage.stream()
                .map(l -> modelMapper.map(l, LikeResponse.class))
                .toList();
        Pagination pagination = new Pagination(
                likesPage.getTotalPages(),
                likesPage.getTotalElements(),
                likesPage.getNumber() + 1
        );
        return new ImmutablePair<>(likeResponseList, pagination);
    }
}