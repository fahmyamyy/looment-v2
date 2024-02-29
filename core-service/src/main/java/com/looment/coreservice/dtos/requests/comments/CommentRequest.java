package com.looment.coreservice.dtos.requests.comments;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest implements Serializable {
    @NotNull
    private UUID parent;
    @NotNull
    private UUID users;
    @NotNull
    private String comment;
    private List<MultipartFile> attachments;
}
