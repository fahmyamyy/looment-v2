package com.looment.coreservice.exceptions;


import com.looment.coreservice.dtos.ExceptionResponse;
import com.looment.coreservice.exceptions.comments.CommentNotExists;
import com.looment.coreservice.exceptions.posts.PostInvalidCategory;
import com.looment.coreservice.exceptions.posts.PostNotExists;
import com.looment.coreservice.exceptions.users.*;
import com.looment.coreservice.exceptions.usersinfo.UserInfoNotExists;
import com.looment.coreservice.utils.ExceptionController;
import com.looment.coreservice.utils.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class APIExceptionHandler extends ExceptionController {

    @ExceptionHandler(value = ServerException.class)
    public ResponseEntity<ExceptionResponse> handlerGeneralError() {
        log.error(ResponseCode.INTERNAL_ERROR.message());
        return responseInternalError(ResponseCode.INTERNAL_ERROR.code(), ResponseCode.INTERNAL_ERROR.message());
    }

    @ExceptionHandler(value = FailedMapping.class)
    public ResponseEntity<ExceptionResponse> handlerFailedMapping() {
        log.error(ResponseCode.FAILED_MAPPING.message());
        return responseConflict(ResponseCode.FAILED_MAPPING.code(), ResponseCode.FAILED_MAPPING.message());
    }

    @ExceptionHandler(value = FailedRestCall.class)
    public ResponseEntity<ExceptionResponse> handlerFailedRestCall() {
        log.error(ResponseCode.FAILED_REST_CALL.message());
        return responseConflict(ResponseCode.FAILED_REST_CALL.code(), ResponseCode.FAILED_REST_CALL.message());
    }

    @ExceptionHandler(value = ParentNotExists.class)
    public ResponseEntity<ExceptionResponse> handlerParentNotExists() {
        log.error(ResponseCode.PARENT_NOT_EXISTS.message());
        return responseConflict(ResponseCode.PARENT_NOT_EXISTS.code(), ResponseCode.PARENT_NOT_EXISTS.message());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handlerMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error ->
                errorMap.put(error.getField(), error.getDefaultMessage())
        );
        log.error(ResponseCode.BAD_REQUEST.message());
        return responseBadRequestInfo(ResponseCode.BAD_REQUEST.code(), errorMap);
    }

    //Comment
    @ExceptionHandler(value = CommentNotExists.class)
    public ResponseEntity<ExceptionResponse> handlerCommentNotExists() {
        log.error(ResponseCode.COMMENT_NOT_EXISTS.message());
        return responseConflict(ResponseCode.COMMENT_NOT_EXISTS.code(), ResponseCode.COMMENT_NOT_EXISTS.message());
    }

    //Post
    @ExceptionHandler(value = PostNotExists.class)
    public ResponseEntity<ExceptionResponse> handlerPostNotExists() {
        log.error(ResponseCode.POSTS_NOT_EXISTS.message());
        return responseConflict(ResponseCode.POSTS_NOT_EXISTS.code(), ResponseCode.POSTS_NOT_EXISTS.message());
    }

    @ExceptionHandler(value = PostInvalidCategory.class)
    public ResponseEntity<ExceptionResponse> handlerPostInvalidCategory() {
        log.error(ResponseCode.POSTS_CATEGORY_INVALID.message());
        return responseConflict(ResponseCode.POSTS_CATEGORY_INVALID.code(), ResponseCode.POSTS_CATEGORY_INVALID.message());
    }

    // User
    @ExceptionHandler(value = UserEmailExists.class)
    public ResponseEntity<ExceptionResponse> handlerUserEmailExists() {
        log.error(ResponseCode.USER_EMAIL_CONFLICT.message());
        return responseConflict(ResponseCode.USER_EMAIL_CONFLICT.code(), ResponseCode.USER_EMAIL_CONFLICT.message());
    }

    @ExceptionHandler(value = UserUsernameExists.class)
    public ResponseEntity<ExceptionResponse> handlerUserUsernameExists() {
        log.error(ResponseCode.USER_USERNAME_CONFLICT.message());
        return responseConflict(ResponseCode.USER_USERNAME_CONFLICT.code(), ResponseCode.USER_USERNAME_CONFLICT.message());
    }

    @ExceptionHandler(value = UserEmailInvalid.class)
    public ResponseEntity<ExceptionResponse> handlerUserEmailInvalid() {
        log.error(ResponseCode.USER_EMAIL_INVALID.message());
        return responseBadRequest(ResponseCode.USER_EMAIL_INVALID.code(), ResponseCode.USER_EMAIL_INVALID.message());
    }

    @ExceptionHandler(value = UserUsernameInvalid.class)
    public ResponseEntity<ExceptionResponse> handlerUserUsernameInvalid() {
        log.error(ResponseCode.USER_USERNAME_INVALID.message());
        return responseBadRequest(ResponseCode.USER_USERNAME_INVALID.code(), ResponseCode.USER_USERNAME_INVALID.message());
    }

    @ExceptionHandler(value = UserFullnameInvalid.class)
    public ResponseEntity<ExceptionResponse> handlerUserFullnameInvalid() {
        log.error(ResponseCode.USER_FULLNAME_INVALID.message());
        return responseBadRequest(ResponseCode.USER_FULLNAME_INVALID.code(), ResponseCode.USER_FULLNAME_INVALID.message());
    }

    @ExceptionHandler(value = UserPasswordInvalid.class)
    public ResponseEntity<ExceptionResponse> handlerUserPasswordInvalid() {
        log.error(ResponseCode.USER_PASSWORD_INVALID.message());
        return responseBadRequest(ResponseCode.USER_PASSWORD_INVALID.code(), ResponseCode.USER_PASSWORD_INVALID.message());
    }

    @ExceptionHandler(value = UserUnderage.class)
    public ResponseEntity<ExceptionResponse> handlerUserUnderage() {
        log.error(ResponseCode.USER_UNDERAGE.message());
        return responseBadRequest(ResponseCode.USER_UNDERAGE.code(), ResponseCode.USER_UNDERAGE.message());
    }

    @ExceptionHandler(value = UserOtpInvalid.class)
    public ResponseEntity<ExceptionResponse> handlerUserOtpInvalid() {
        log.error(ResponseCode.USER_OTP_INVALID.message());
        return responseUnauthorized(ResponseCode.USER_OTP_INVALID.code(), ResponseCode.USER_OTP_INVALID.message());
    }

    @ExceptionHandler(value = UserOtpExpired.class)
    public ResponseEntity<ExceptionResponse> handlerUserOtpExpired() {
        log.error(ResponseCode.USER_OTP_EXPIRED.message());
        return responseUnauthorized(ResponseCode.USER_OTP_EXPIRED.code(), ResponseCode.USER_OTP_EXPIRED.message());
    }

    @ExceptionHandler(value = UserNotExists.class)
    public ResponseEntity<ExceptionResponse> handlerUserNotExists() {
        log.error(ResponseCode.USER_NOT_EXISTS.message());
        return responseNotFound(ResponseCode.USER_NOT_EXISTS.code(), ResponseCode.USER_NOT_EXISTS.message());
    }

    //UserInfo
    @ExceptionHandler(value = UserInfoNotExists.class)
    public ResponseEntity<ExceptionResponse> handlerUserInfoNotExists() {
        log.error(ResponseCode.USERINFO_NOT_EXISTS.message());
        return responseConflict(ResponseCode.USERINFO_NOT_EXISTS.code(), ResponseCode.USERINFO_NOT_EXISTS.message());
    }
}
