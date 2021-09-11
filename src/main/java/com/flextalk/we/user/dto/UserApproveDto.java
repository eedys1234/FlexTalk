package com.flextalk.we.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class UserApproveDto {

    @NotNull
    @JsonProperty(value = "user_id")
    private Long userId;

}
