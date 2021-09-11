package com.flextalk.we.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class UserRoleGrantDto {

    @NotNull
    @JsonProperty(value = "user_id")
    private Long userId;

    @NotBlank
    @JsonProperty(value = "user_grant_role")
    private String userGrantRole;
}
