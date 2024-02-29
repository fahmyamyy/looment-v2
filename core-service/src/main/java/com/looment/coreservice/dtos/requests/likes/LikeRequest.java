package com.looment.coreservice.dtos.requests.likes;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeRequest implements Serializable {
    @NotNull
    private UUID users;
    @NotNull
    private UUID parent;
}
