package com.looment.coreservice.dtos.responses.follows;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "userId"})
public class FollowResponse implements Serializable {
    private UUID id;
    @JsonProperty("userId")
    private UUID usersId;
    private String username;
    private String profileUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private LocalDateTime createdAt;
}