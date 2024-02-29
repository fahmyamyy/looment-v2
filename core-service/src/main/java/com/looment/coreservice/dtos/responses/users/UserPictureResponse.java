package com.looment.coreservice.dtos.responses.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPictureResponse implements Serializable {
    private String imageUrl;
}
