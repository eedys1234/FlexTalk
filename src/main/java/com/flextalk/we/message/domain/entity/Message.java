package com.flextalk.we.message.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "parentMessage")
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
