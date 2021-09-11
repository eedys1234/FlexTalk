package com.flextalk.we.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserUpdateDto {

    @JsonProperty(value = "user_id")
    private Long userId;

    @JsonProperty(value = "user_password")
    private String userPassword;

    @JsonProperty(value = "user_profile")
    private String userProfile;
}
