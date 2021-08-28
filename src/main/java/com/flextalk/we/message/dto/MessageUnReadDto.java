package com.flextalk.we.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MessageUnReadDto {

    @JsonProperty(value = "message_id")
    private Long messageId;

    @JsonProperty(value = "message_count")
    private Long messageCount;


    public MessageUnReadDto(Long messageId, Long messageCount) {
        this.messageId = messageId;
        this.messageCount = messageCount;
    }
}
