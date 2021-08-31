package com.flextalk.we.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"messageId", "messageUnReadCount"})
public class MessageUnReadResponseDto {

    @JsonProperty(value = "message_id")
    private Long messageId;

    @JsonProperty(value = "message_unread_count")
    private Long messageUnReadCount;


    public MessageUnReadResponseDto(Long messageId, Long messageUnReadCount) {
        this.messageId = messageId;
        this.messageUnReadCount = messageUnReadCount;
    }
}
