package com.looment.coreservice.services.implementation;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.UploadRequest;
import com.looment.coreservice.dtos.requests.users.UserRequest;
import com.looment.coreservice.dtos.requests.users.UserUpdateRequest;
import com.looment.coreservice.dtos.responses.users.UserResponse;
import com.looment.coreservice.dtos.responses.users.UserDetailResponse;
import com.looment.coreservice.dtos.responses.users.UserPictureResponse;
import com.looment.coreservice.dtos.responses.users.UserSimpleResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse updateUser(UUID userId, UserUpdateRequest userUpdateRequest);
    UserPictureResponse userPicture(UUID userId, UploadRequest uploadRequest);
    UserDetailResponse getUserById(UUID userId);
    Pair<List<UserSimpleResponse>, Pagination> searchUsername(Pageable pageable, String username);
    void togglePrivate(UUID userId);
}
