package com.flextalk.we.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MessageUnReadResponseDto {

    @JsonProperty(value = "message_id")
    private Long messageId;

    @JsonProperty(value = "message_count")
    private Long messageCount;


    public MessageUnReadResponseDto(Long messageId, Long messageCount) {
        this.messageId = messageId;
        this.messageCount = messageCount;
    }
}
