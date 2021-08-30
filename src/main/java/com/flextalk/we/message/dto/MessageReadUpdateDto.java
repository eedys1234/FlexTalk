package com.flextalk.we.message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class MessageReadUpdateDto {

    @NotBlank
    private String messageIds;

    @NotNull
    private Long participantId;

}
