package com.flextalk.we.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class UserRegisterDto {

    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-z]{2,6}$")
    @NotBlank
    @JsonProperty(value = "user_email")
    private String userEmail;

    @Pattern(regexp = "[a-zA-Z0-9!@#$%^&*?_~]{8,12}")
    @NotBlank
    @JsonProperty(value = "user_password")
    private String userPassword;
}
