package com.flextalk.we.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
public class UserLoginRequestDto {

    @NotBlank
    @JsonProperty(value = "user_email")
    private String userEmail;

    @NotBlank
    @JsonProperty(value = "user_password")
    private String userPassword;

}
