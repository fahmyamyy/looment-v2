package com.looment.coreservice.services;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.UploadRequest;
import com.looment.coreservice.dtos.UploadResponse;
import com.looment.coreservice.dtos.requests.users.UserRequest;
import com.looment.coreservice.dtos.requests.users.UserUpdateRequest;
import com.looment.coreservice.dtos.responses.users.UserResponse;
import com.looment.coreservice.dtos.responses.users.UserDetailResponse;
import com.looment.coreservice.dtos.responses.users.UserPictureResponse;
import com.looment.coreservice.dtos.responses.users.UserSimpleResponse;
import com.looment.coreservice.entities.Users;
import com.looment.coreservice.entities.UsersInfo;
import com.looment.coreservice.exceptions.users.UserFullnameInvalid;
import com.looment.coreservice.exceptions.users.UserNotExists;
import com.looment.coreservice.exceptions.users.UserUsernameExists;
import com.looment.coreservice.exceptions.users.UserUsernameInvalid;
import com.looment.coreservice.repositories.UsersInfoRepository;
import com.looment.coreservice.repositories.UsersRepository;
import com.looment.coreservice.services.implementation.IUserService;
import com.looment.coreservice.utils.AlphabeticalValidator;
import com.looment.coreservice.utils.UsernameValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"user"})
public class UserService implements IUserService {
    private final UsersRepository usersRepository;
    private final UsersInfoRepository usersInfoRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final RestCallService restCallService;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        Users newUsers = modelMapper.map(userRequest, Users.class);

        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setUsers(newUsers);

        usersRepository.save(newUsers);
        usersInfoRepository.save(usersInfo);

        return modelMapper.map(newUsers, UserResponse.class);
    }

    @Override
    @Caching( evict = {
            @CacheEvict(value = "user", allEntries = true),
            @CacheEvict(value = "users", allEntries = true)
    })
    public UserResponse updateUser(UUID userId, UserUpdateRequest userUpdateRequest) {
        if (Boolean.FALSE.equals(UsernameValidator.isValid(userUpdateRequest.getUsername()))) {
            throw new UserUsernameInvalid();
        }
        if (Boolean.TRUE.equals(AlphabeticalValidator.isValid(userUpdateRequest.getFullname()))) {
            throw new UserFullnameInvalid();
        }

        Users updatedUsers = usersRepository.findById(userId)
                .orElseThrow(UserNotExists::new);

        if (!userUpdateRequest.getUsername().equals(updatedUsers.getUsername())) {
            updatedUsers.setUsername(userUpdateRequest.getUsername());
            Optional<Users> username = usersRepository.findByUsernameEqualsIgnoreCase(userUpdateRequest.getUsername());
            if (username.isPresent()) {
                throw new UserUsernameExists();
            }
        }

        updatedUsers.setFullname(userUpdateRequest.getFullname());
        updatedUsers.setBio(userUpdateRequest.getBio());
        updatedUsers.setDob(userUpdateRequest.getDob());
        usersRepository.save(updatedUsers);

        return modelMapper.map(updatedUsers, UserResponse.class);
    }

    @Override
    @Caching( evict = {
            @CacheEvict(value = "user", allEntries = true),
            @CacheEvict(value = "users", allEntries = true)
    })
    public UserPictureResponse userPicture(UUID userId, UploadRequest uploadRequest) {
        Users users = usersRepository.findById(userId)
                .orElseThrow(UserNotExists::new);

        UploadResponse responseUpload = restCallService.callApiUpload(uploadRequest);
        users.setProfileUrl(responseUpload.getUrl());
        usersRepository.save(users);

        return new UserPictureResponse(users.getProfileUrl());

    }

    @Override
    @Cacheable(value = "user")
    public UserDetailResponse getUserById(UUID userId) {
        Users users = usersRepository.findById(userId)
                .orElseThrow(UserNotExists::new);

        return modelMapper.map(users, UserDetailResponse.class);
    }

    @Override
    @Cacheable(value = "users")
    public Pair<List<UserSimpleResponse>, Pagination> searchUsername(Pageable pageable, String username) {
        Page<Users> usersPage = usersRepository.findByUsernameContainsIgnoreCase(pageable, username);

        List<UserSimpleResponse> userResponseList = usersPage.stream()
                .map(user -> modelMapper.map(user, UserSimpleResponse.class))
                .toList();
        Pagination pagination = new Pagination(
                usersPage.getTotalPages(),
                usersPage.getTotalElements(),
                usersPage.getNumber() + 1
        );
        return new ImmutablePair<>(userResponseList, pagination);
    }

    @Override
    public void togglePrivate(UUID userId) {
        Users users = usersRepository.findById(userId)
                .orElseThrow(UserNotExists::new);

        users.setIsPrivate(!users.getIsPrivate());
        usersRepository.save(users);
    }
}
