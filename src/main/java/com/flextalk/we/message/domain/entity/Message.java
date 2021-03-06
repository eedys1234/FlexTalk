package com.flextalk.we.message.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.cmmn.file.FileManager;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Entity
@Table(name = "ft_message")
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "message_content", columnDefinition = "TEXT")
    private String messageContent;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageRead> messageReads = new ArrayList<>();

    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.PERSIST)
    private List<Message> childMessages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "message", cascade = CascadeType.ALL)
    private MessageFile messageFile;

    private Message(Participant participant, Room room, String messageContent, String messageType) {
        this.participant = Objects.requireNonNull(participant);
        this.room = Objects.requireNonNull(room);
        this.messageContent = Objects.requireNonNull(messageContent);
        this.messageType = MessageType.valueOf(Objects.requireNonNull(messageType.toUpperCase()));
        this.isDelete = Boolean.FALSE;
    }

    private Message(Participant participant, Room room, String messageContent, String messageType,
                    String filePath, String orgFileName) {

        this(participant, room, messageContent, messageType);

        //FILE일 경우
        if(MessageType.valueOf(messageType) == MessageType.FILE) {
            this.messageFile = MessageFile.of(this, filePath, orgFileName);
        }
    }

    /**
     * 메시지 생성함수(TEXT)
     * @param participant 메시지 보낸 참여자
     * @param room 메시지가 발생한 채팅방
     * @param content 메시지 내용
     * @return 메시지
     */
    public static Message create(Participant participant, Room room, String content, String messageType) {
        Message message = new Message(participant, room, content, messageType);

        if(message.getMessageType() == MessageType.FILE) {
            throw new IllegalArgumentException("파일경로와 원본파일명을 추가해주세요.");
        }

        return message;
    }

    /**
     * 메시지 생성함수(FILE)
     * @param participant 메시지 보낸 참여자
     * @param room 메시지가 발생한 채팅방
     * @param content 메시지 내용
     * @return 메시지
     */
    public static Message create(Participant participant, Room room, String content, String messageType,
                                 String filePath, String orgFileName) {

        Message message = new Message(participant, room, content, messageType, filePath, orgFileName);
        return message;
    }
    
    /**
     * 메시지 읽음
     * @param otherParticipant 메시지를 보낸 사용자를 제외한 참여자
     * @return Message ID
     * @throws IllegalArgumentException 메시지를 보낸 참여자가 메시지를 읽음
     */
    public Long read(Participant otherParticipant) {

        if(otherParticipant.equals(this.participant)) {
            throw new IllegalArgumentException("메시지를 보낸 사용자입니다. userId = " + otherParticipant.getId());
        }

        MessageRead messageRead = MessageRead.of(otherParticipant, this);
        this.messageReads.add(messageRead);
        return this.id;
    }

    /**
     * 기존 메시지에 코멘트 달기
     * @param message 자식 메시지
     */
    public void comment(Message message) {
        this.childMessages.add(message);
        message.parentMessage = this;
    }

    /**
     * 파일 저장
     * 파일 저장 오류가 일시적인 경우(IOException) 0.5초간격으로 3번 Retry
     * @param file 저장하고자하는 파일 byte
     * @return 저장 성공 여부
     * @throws IOException 파일 저장 실패
     */
    @Retryable(
            value = { IOException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 500L)
    )
    public boolean saveFile(byte[] file) {

        if(this.messageType != MessageType.FILE) {
            throw new IllegalArgumentException("메시지 형태가 파일이 아닙니다.");
        }

        MessageFile messageFile = Objects.requireNonNull(this.messageFile);
        boolean isCreated = FileManager.create(messageFile.getFilePath(), String.join(".", messageFile.getNewFileName(), messageFile.getFileExt()), file);

        messageFile.updateFileSize(FileManager.extractFileSize(String.join(File.separator, messageFile.getFilePath(),
                String.join(".", messageFile.getNewFileName(), messageFile.getFileExt()))));

        return isCreated;
    }


    /**
     * 파일삭제
     * 파일 삭제가 일시적인 오류(IOException)인 경우 0.5초 간격으로 3번 Retry
     * @return 파일삭제 성공여부
     * @throws IOException 파일삭제 실패 시
     */
    @Retryable(
            value = { IOException.class },
            maxAttempts = 3,
            backoff = @Backoff(value = 500L)
    )
    public boolean deleteFile() {

        if(this.messageType != MessageType.FILE) {
            throw new IllegalArgumentException("메시지 형태가 파일이 아닙니다.");
        }

        MessageFile messageFile = Objects.requireNonNull(this.messageFile);
        return FileManager.delete(messageFile.getFilePath(), String.join(".", messageFile.getNewFileName(), messageFile.getFileExt()));
    }

    /**
     * 메시지 삭제
     */
    public void delete() {
        this.isDelete = true;
    }

    public enum MessageType {
        TEXT,
        FILE
    }
}
