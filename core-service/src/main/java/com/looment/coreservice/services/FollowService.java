package com.looment.coreservice.services;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.requests.follows.FollowRequest;
import com.looment.coreservice.dtos.responses.follows.FollowResponse;
import com.looment.coreservice.entities.Follows;
import com.looment.coreservice.entities.Users;
import com.looment.coreservice.entities.UsersInfo;
import com.looment.coreservice.exceptions.users.UserNotExists;
import com.looment.coreservice.exceptions.usersinfo.UserInfoNotExists;
import com.looment.coreservice.repositories.FollowsRepository;
import com.looment.coreservice.repositories.UsersInfoRepository;
import com.looment.coreservice.repositories.UsersRepository;
import com.looment.coreservice.services.implementation.IFollowService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"follow"})
public class FollowService implements IFollowService {
    private final FollowsRepository followsRepository;
    private final UsersRepository usersRepository;
    private final UsersInfoRepository usersInfoRepository;

    @Override
    @Cacheable(value = "followers")
    public Pair<List<FollowResponse>, Pagination> getFollowers(Pageable pageable, UUID userId) {
        Page<Follows> followsPage = followsRepository.findFollowers(pageable, userId);

        List<FollowResponse> userResponseList = followsPage.stream()
                .map(followPage -> {
                    FollowResponse followResponse = new FollowResponse();
                    followResponse.setId(followPage.getId());
                    followResponse.setUsersId(followPage.getFollower().getId());
                    followResponse.setUsername(followPage.getFollower().getUsername());
                    followResponse.setProfileUrl(followPage.getFollower().getProfileUrl());
                    followResponse.setCreatedAt(followPage.getFollower().getCreatedAt());
                    return followResponse;
                }).toList();
        Pagination pagination = new Pagination(
                followsPage.getTotalPages(),
                followsPage.getTotalElements(),
                followsPage.getNumber() + 1
        );
        return new ImmutablePair<>(userResponseList, pagination);
    }

    @Override
    @Cacheable(value = "followings")
    public Pair<List<FollowResponse>, Pagination> getFollowings(Pageable pageable, UUID userId) {
        Page<Follows> followsPage = followsRepository.findFollowings(pageable, userId);

        List<FollowResponse> userResponseList = followsPage.stream()
                .map(followPage -> {
                    FollowResponse followResponse = new FollowResponse();
                    followResponse.setId(followPage.getId());
                    followResponse.setUsersId(followPage.getFollowed().getId());
                    followResponse.setUsername(followPage.getFollowed().getUsername());
                    followResponse.setProfileUrl(followPage.getFollowed().getProfileUrl());
                    followResponse.setCreatedAt(followPage.getFollowed().getCreatedAt());
                    return followResponse;
                }).toList();
        Pagination pagination = new Pagination(
                followsPage.getTotalPages(),
                followsPage.getTotalElements(),
                followsPage.getNumber() + 1
        );
        return new ImmutablePair<>(userResponseList, pagination);
    }

    @Override
    @Caching( evict = {
            @CacheEvict(value = "follow", allEntries = true),
            @CacheEvict(value = "followers", allEntries = true),
            @CacheEvict(value = "followings", allEntries = true),
            @CacheEvict(value = "posts", allEntries = true)
    })
    public void toggleFollow(FollowRequest followRequest) {
        Pair<List<Users>, List<UsersInfo>> users = validateUsers(followRequest.getFollowedId(), followRequest.getFollowerId());
        UsersInfo followed = users.getRight().get(0);
        UsersInfo follower = users.getRight().get(1);

        Optional<Follows> optionalFollows = followsRepository.findByFollowed_IdEqualsAndFollower_IdEquals(followRequest.getFollowedId(), followRequest.getFollowerId());

        Follows follows = new Follows();
        LocalDateTime now = LocalDateTime.now();

        if (optionalFollows.isPresent()) {
            follows = optionalFollows.get();
            if (follows.getDeletedAt() == null) {
                follows.setDeletedAt(now);
                followed.setFollowers(followed.getFollowers() - 1);
                follower.setFollowings(follower.getFollowings() - 1);
            } else {
                follows.setUpdatedAt(now);
                follows.setDeletedAt(null);
                followed.setFollowers(followed.getFollowers() + 1);
                follower.setFollowings(follower.getFollowings() + 1);
            }
        } else {
            follows.setFollowed(users.getLeft().get(0));
            follows.setFollower(users.getLeft().get(1));
            followed.setFollowers(followed.getFollowers() + 1);
            follower.setFollowings(follower.getFollowings() + 1);
        }

        followsRepository.save(follows);
        usersInfoRepository.saveAll(List.of(followed, follower));
    }

    @Override
    public List<UUID> getFollowingsId(UUID userId) {
        return followsRepository.findFollowings(userId);
    }

    private Pair<List<Users>, List<UsersInfo>> validateUsers(UUID userOne, UUID userTwo) {
        Users optionalOne = usersRepository.findById(userOne)
                .orElseThrow(UserNotExists::new);

        Users optionalTwo = usersRepository.findById(userTwo)
                .orElseThrow(UserNotExists::new);

        UsersInfo infoOne = usersInfoRepository.findById(optionalOne.getId())
                .orElseThrow(UserInfoNotExists::new);

        UsersInfo infoTwo = usersInfoRepository.findById(optionalTwo.getId())
                .orElseThrow(UserInfoNotExists::new);

        return new ImmutablePair<>(List.of(optionalOne, optionalTwo), List.of(infoOne, infoTwo));
    }
}
