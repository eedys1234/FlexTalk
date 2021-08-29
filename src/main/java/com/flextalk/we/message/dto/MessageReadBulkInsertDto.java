package com.flextalk.we.message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageReadBulkInsertDto {

    private Long participantId;
    private Long messageId;

    public MessageReadBulkInsertDto(final Long participantId, final Long messageId) {
        this.participantId = participantId;
        this.messageId = messageId;
    }
}
