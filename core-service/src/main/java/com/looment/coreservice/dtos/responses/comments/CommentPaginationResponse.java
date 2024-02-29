package com.looment.coreservice.dtos.responses.comments;

import com.looment.coreservice.dtos.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPaginationResponse<T> implements Serializable {
    private T comments;
    private Pagination pagination;
}