package com.flextalk.we.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flextalk.we.user.domain.entity.User;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UserRegisterDto {

    @NotBlank
    @JsonProperty(value = "user_email")
    private String userEmail;

    @NotBlank
    @JsonProperty(value = "user_password")
    private String userPassword;

    public User toEntity() {
        return User.register(this.userEmail, this.userPassword);
    }
}
