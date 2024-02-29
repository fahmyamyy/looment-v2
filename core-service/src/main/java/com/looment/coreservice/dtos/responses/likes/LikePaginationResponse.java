package com.looment.coreservice.dtos.responses.likes;

import com.looment.coreservice.dtos.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikePaginationResponse<T> implements Serializable {
    private T answers;
    private Pagination pagination;
}