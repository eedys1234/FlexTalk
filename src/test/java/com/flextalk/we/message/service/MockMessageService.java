package com.flextalk.we.message.service;

import com.flextalk.we.message.domain.repository.MessageReadJdbcRepository;
import com.flextalk.we.message.domain.repository.MessageReadRepository;
import com.flextalk.we.message.domain.repository.MessageRepository;
import com.flextalk.we.message.dto.MessageSaveRequestDto;
import com.flextalk.we.participant.service.ParticipantService;
import com.flextalk.we.room.service.RoomService;

public class MockMessageService extends MessageService {

    public MockMessageService(RoomService roomService, ParticipantService participantService, MessageRepository messageRepository, MessageReadRepository messageReadRepository,
                              MessageReadJdbcRepository messageReadJdbcRepository, String messageFilePath) {
        super(roomService, participantService, messageRepository, messageReadRepository, messageReadJdbcRepository);
        super.messageFilePath = messageFilePath;
    }

    @Override
    public Long sendFileMessage(Long roomId, Long participantId, MessageSaveRequestDto messageSaveRequestDto, String orgFileName, byte[] file) {
        return super.sendFileMessage(roomId, participantId, messageSaveRequestDto, orgFileName, file);
    }
}
