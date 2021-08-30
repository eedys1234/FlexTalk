package com.flextalk.we.message.service;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.cmmn.file.FileManager;
import com.flextalk.we.message.domain.entity.Message;
import com.flextalk.we.message.domain.repository.MessageReadRepository;
import com.flextalk.we.message.domain.repository.MessageRepository;
import com.flextalk.we.message.dto.*;
import com.flextalk.we.participant.domain.entity.Participant;
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
    public Long sendTextMessage(Long roomId, Long participantId, MessageSaveRequestDto messageSaveRequestDto) {

        final Room room = roomService.findRoomAddedAddiction(roomId);

        final Participant participant = participantService.findParticipant(participantId);

        Message message = Message.create(participant, room, messageSaveRequestDto.getMessageContent(), messageSaveRequestDto.getMessageType());
        Message sendMessage = messageRepository.save(message);
        return sendMessage.getId();
    }

    /**
     * 메시지 생성(FILE)
     * @param roomId 채팅방 ID
     * @param participantId 참여자 ID
     * @param messageSaveRequestDto 메시지 생성 Dto
     * @return 생성된 메시지 ID
     */
    @Transactional
    public Long sendFileMessage(Long roomId, Long participantId, MessageSaveRequestDto messageSaveRequestDto, String orgFileName, byte[] file) {

        final Room room = roomService.findRoomAddedAddiction(roomId);

        final Participant participant = participantService.findParticipant(participantId);

        Message message = Message.create(participant, room, messageSaveRequestDto.getMessageContent(), messageSaveRequestDto.getMessageType(),
                messageFilePath, orgFileName);

        message.saveFile(file);
        Message sendMessage = messageRepository.save(message);
        return sendMessage.getId();
    }

    /**
     * 메시지 삭제
     * @param messageId 메시지 ID
     * @param participantId 참여자 ID
     * @param roomId 채팅방 ID
     * @return 삭제된 메시지 ID
     */
    @Transactional
    public Long deleteMessage(Long messageId, Long participantId, Long roomId) {

        Message message = messageRepository.findOne(messageId, participantId, roomId)
                .orElseThrow(() -> new NotEntityException("메시지가 존재하지 않습니다."));

        message.delete();
        
        if(message.getMessageType() == Message.MessageType.FILE) {
            message.deleteFile();
        }

        return message.getId();
    }


    /**
     * 메시지 안읽은 수 조회
     * @param roomId 채팅방 ID
     * @param messageIds 메시지's ID
     * @return 메시지 별 안읽은 수
     */
    @Transactional(readOnly = true)
    public List<MessageUnReadResponseDto> unReadMessagesCount(Long roomId, String messageIds) {

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
     * @param messageReadUpdateDto 메시지's ID
     * @return 메시지 읽음 성공여부
     */
    @Transactional
    public Long readMessage(MessageReadUpdateDto messageReadUpdateDto) {

        Participant participant = participantService.findParticipant(messageReadUpdateDto.getParticipantId());

        String[] splitMessageIds = messageReadUpdateDto.getMessageIds().split(",");

        List<MessageReadBulkInsertDto> messageReads = Arrays.stream(splitMessageIds)
                .map(id -> new MessageReadBulkInsertDto(messageReadUpdateDto.getParticipantId(), Long.parseLong(id)))
                .collect(toList());

        messageReadRepository.saveAll(messageReads);

        return 1L;
    }

}
