package com.looment.coreservice.dtos.responses.posts;

import com.looment.coreservice.dtos.responses.comments.CommentPaginationResponse;
import com.looment.coreservice.dtos.responses.comments.CommentResponse;
import com.looment.coreservice.dtos.responses.likes.LikeResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "userId"})
public class PostInfoResponse implements Serializable {
    private UUID id;
    @JsonProperty("userId")
    private UUID usersId;
    private String caption;
    private String category;
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