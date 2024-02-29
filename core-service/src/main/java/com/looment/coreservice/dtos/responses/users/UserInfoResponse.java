package com.looment.coreservice.dtos.responses.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse implements Serializable {
    private Integer followers;
    private Integer followings;
    private Integer totalPosts;
    private Integer totalLikes;
}
