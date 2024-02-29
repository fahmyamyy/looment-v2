package com.looment.coreservice.dtos.responses.comments;

import com.looment.coreservice.dtos.Pagination;
import com.looment.coreservice.dtos.responses.likes.LikeResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "parentId", "userId"})
public class CommentInfoResponse implements Serializable {
    private UUID id;
    @JsonProperty("parentId")
    private UUID parent;
    @JsonProperty("userId")
    private UUID usersId;
    private String comment;
    private List<String> url;
    private Integer totalLikes;
    private Integer totalComments;
    private Integer totalViews;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime updatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime deletedAt;
    private CommentPaginationResponse commentList;
}