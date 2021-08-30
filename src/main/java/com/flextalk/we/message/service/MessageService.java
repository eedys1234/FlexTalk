package com.flextalk.we.message.service;

import com.flextalk.we.cmmn.file.FileManager;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.domain.repository.MessageReadRepository;
import com.flextalk.we.message.domain.repository.MessageRepository;
import com.flextalk.we.message.dto.*;
import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.participant.service.ParticipantService;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

//TODO : Kafka Producing Code 추가 필요
@Service
@RequiredArgsConstructor
public class MessageService {

    @Value("${message_file_path}")
    private String messageFilePath;

    private final RoomService roomService;
    private final ParticipantService participantService;
    private final MessageRepository messageRepository;
    private final MessageReadRepository messageReadRepository;

    /**
     * 메시지 생성(TEXT)
     * @param roomId 채팅방 ID
     * @param participantId 참여자 ID
     * @param messageSaveRequestDto 메시지 생성 Dto
     * @return 생성된 메시지 ID
     */
    @Transactional
    public Long createTextMessage(Long roomId, Long participantId, MessageSaveRequestDto messageSaveRequestDto) {

        final Room room = roomService.findRoomAddedAddiction(roomId);

        final Participant participant = participantService.findParticipant(participantId);

        Message message = Message.create(participant, room, messageSaveRequestDto.getMessageContent(), messageSaveRequestDto.getMessageType());
        Message createdMessage = messageRepository.save(message);

        return createdMessage.getId();
    }

    /**
     * 메시지 생성(FILE)
     * @param roomId 채팅방 ID
     * @param participantId 참여자 ID
     * @param messageSaveRequestDto 메시지 생성 Dto
     * @return 생성된 메시지 ID
     */
    @Transactional
    public Long createFileMessage(Long roomId, Long participantId, MessageSaveRequestDto messageSaveRequestDto, String orgFileName, byte[] file) {

        final Room room = roomService.findRoomAddedAddiction(roomId);

        final Participant participant = participantService.findParticipant(participantId);

        Message message = Message.create(participant, room, messageSaveRequestDto.getMessageContent(), messageSaveRequestDto.getMessageType(),
                messageFilePath, orgFileName);

        Message createdMessage = messageRepository.save(message);

        if(!FileManager.create(messageFilePath, orgFileName, file)) {
            throw new IllegalStateException("파일저장이 실패하였습니다. orgFileName = " + orgFileName);
        }

        return createdMessage.getId();
    }

    /**
     * 메시지 안읽은 수 조회
     * @param roomId 채팅방 ID
     * @param messageIds 메시지's ID
     * @return 메시지 별 안읽은 수
     */
    @Transactional(readOnly = true)
    public List<MessageUnReadResponseDto> unReadCountMessages(Long roomId, String messageIds) {

        final Room room = roomService.findRoomAddedAddiction(roomId);
        final String[] splitMessageIds = messageIds.split(",");

        final List<Participant> participants = room.participants();

        List<MessageReadResponseDto> reads = messageReadRepository.findByMessages(Arrays.stream(splitMessageIds)
                .map(id -> Long.parseLong(id))
                .collect(toList()));

        return reads.stream()
                .map(read -> new MessageUnReadResponseDto(read.getMessageId(), participants.size() - read.getMessageReadCount() - 1))
                .collect(toList());
    }

    /**
     * 메시지 읽음
     * @param participantId 참여자 ID
     * @param messageReadUpdateDto 메시지's ID
     * @return 읽은 메시지 ID
     */
    @Transactional
    public List<Long> readMessage(Long participantId, MessageReadUpdateDto messageReadUpdateDto) {

        Participant participant = participantService.findParticipant(participantId);

        String[] splitMessageIds = messageReadUpdateDto.getMessageIds().split(",");

        List<MessageReadBulkInsertDto> messageReads = Arrays.stream(splitMessageIds)
                .map(id -> new MessageReadBulkInsertDto(participantId, Long.parseLong(id)))
                .collect(toList());

        messageReadRepository.saveAll(messageReads);

        return messageReads.stream()
                .map(MessageReadBulkInsertDto::getMessageId)
                .collect(toList());
    }

}
