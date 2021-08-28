package com.flextalk.we.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MessageSaveRequestDto {

    @JsonProperty(value = "message_content")
    private String messageContent;

    @NotBlank
    @JsonProperty(value = "message_type")
    private String messageType;
}
