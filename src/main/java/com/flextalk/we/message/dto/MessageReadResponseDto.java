package com.flextalk.we.message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageReadResponseDto {

    private Long messageId;
    private Long messageReadCount;

    public MessageReadResponseDto(Long messageId, Long messageReadCount) {
        this.messageId = messageId;
        this.messageReadCount = messageReadCount;
    }
}
