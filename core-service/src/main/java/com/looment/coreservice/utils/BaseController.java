package com.looment.coreservice.utils;

import com.looment.coreservice.dtos.BaseResponse;
import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.PaginationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

public class BaseController {
    protected ResponseEntity<BaseResponse> responseCreated(String message) {
        return new ResponseEntity<>(BaseResponse.builder()
                .message(message)
                .data(Collections.emptyList())
                .build(), HttpStatus.CREATED);
    }

    protected ResponseEntity<BaseResponse> responseCreated(String message, Object data) {
        return new ResponseEntity<>(BaseResponse.builder()
                .message(message)
                .data(data)
                .build(), HttpStatus.CREATED);
    }

    protected ResponseEntity<BaseResponse> responseSuccess(String message) {
        return new ResponseEntity<>(BaseResponse.builder()
                .message(message)
                .data(Collections.emptyList())
                .build(), HttpStatus.OK);
    }

    protected ResponseEntity<BaseResponse> responseSuccess(String message, Object data) {
        return new ResponseEntity<>(BaseResponse.builder()
                .message(message)
                .data(data)
                .build(), HttpStatus.OK);
    }

    protected ResponseEntity<BaseResponse> responseDelete(String message) {
        return new ResponseEntity<>(BaseResponse.builder()
                .message(message)
                .data(Collections.emptyList())
                .build(), HttpStatus.NO_CONTENT);
    }

    protected PaginationResponse responsePagination(String message, Object data, Pagination pagination) {
        return new PaginationResponse<>(
                message,
                data,
                pagination);
    }
}