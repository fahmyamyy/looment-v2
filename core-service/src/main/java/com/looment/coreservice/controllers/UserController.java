package com.looment.coreservice.controllers;

import com.looment.coreservice.dtos.BaseResponse;
import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.PaginationResponse;
import com.looment.coreservice.dtos.UploadRequest;
import com.looment.coreservice.dtos.requests.users.UserRequest;
import com.looment.coreservice.dtos.requests.users.UserUpdateRequest;
import com.looment.coreservice.dtos.responses.users.UserResponse;
import com.looment.coreservice.dtos.responses.users.UserDetailResponse;
import com.looment.coreservice.dtos.responses.users.UserPictureResponse;
import com.looment.coreservice.dtos.responses.users.UserSimpleResponse;
import com.looment.coreservice.services.UserService;
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
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BaseResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        return responseCreated("Successfully create new User", userResponse);
    }

    @PostMapping("picture/{userId}")
    public ResponseEntity<BaseResponse> userPicture(@ModelAttribute @Valid UploadRequest uploadRequest, @PathVariable UUID userId) {
        UserPictureResponse userPictureResponse = userService.userPicture(userId, uploadRequest);
        return responseSuccess("Successfully upload User picture", userPictureResponse);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<BaseResponse> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest, @PathVariable UUID userId) {
        UserResponse userResponse = userService.updateUser(userId, userUpdateRequest);
        return responseSuccess("Successfully update User info", userResponse);
    }

    @GetMapping("{userId}")
    public ResponseEntity<BaseResponse> getByUserId(@PathVariable UUID userId) {
        UserDetailResponse userDetailResponse = userService.getUserById(userId);
        return responseSuccess("Successfully fetch User info", userDetailResponse);
    }

    @GetMapping("search")
    public PaginationResponse<List<UserSimpleResponse>> searchUsername(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @RequestParam(name = "username") String username
    ) {
        int offset = page - 1;
        Pageable pageable = PageRequest.of(offset, limit);

        Pair<List<UserSimpleResponse>, Pagination> pagination = userService.searchUsername(pageable, username);
        return responsePagination("Successfully fetch all Users username contains: " + username, pagination.getLeft(), pagination.getRight());
    }

    @PatchMapping("private/{userId}")
    public ResponseEntity<BaseResponse> userPrivate(@PathVariable UUID userId) {
        userService.togglePrivate(userId);
        return responseSuccess("Successfully toggled User private");
    }
}
