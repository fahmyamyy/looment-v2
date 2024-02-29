package com.looment.coreservice.dtos.requests.posts;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest implements Serializable {
    @NotNull
    private UUID userId;
    private String caption;
    private String location;
    private Boolean commentable;
}