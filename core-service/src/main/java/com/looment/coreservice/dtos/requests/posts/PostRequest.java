package com.looment.coreservice.dtos.requests.posts;

import jakarta.validation.constraints.NotEmpty;
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
public class PostRequest implements Serializable {
    @NotNull
    private UUID users;
    private String caption;
    private String location;
    private List<MultipartFile> files;
}
