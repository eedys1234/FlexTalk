package com.flextalk.we.message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(of = {"messageId", "participantId"})
public class MessageReadBulkInsertDto {

    private Long participantId;
    private Long messageId;

    public MessageReadBulkInsertDto(final Long participantId, final Long messageId) {
        this.participantId = participantId;
        this.messageId = messageId;
    }
}
