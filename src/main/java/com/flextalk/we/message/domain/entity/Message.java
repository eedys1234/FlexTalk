package com.flextalk.we.message.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_message")
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "message_content", columnDefinition = "TEXT")
    private String content;

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

    @OneToMany(mappedBy = "parent_message")
    private List<Message> child_messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private Message parent_message;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "message", cascade = CascadeType.ALL)
    private MessageFile messageFile;

    private Message(Participant participant, Room room, String content, String messageType) {
        this.participant = Objects.requireNonNull(participant);
        this.room = Objects.requireNonNull(room);
        this.content = Objects.requireNonNull(content);
        this.messageType = MessageType.valueOf(Objects.requireNonNull(messageType));
    }

    /**
     * 메시지 생성함수
     * @param participant 메시지 보낸 참여자
     * @param room 메시지가 발생한 채팅방
     * @param content 메시지 내용
     * @return 메시지
     */
    public static Message create(Participant participant, Room room, String content, String messageType) {
        Message message = new Message(participant, room, content, messageType);
        return message;
    }
    
    /**
     * 메시지 읽음
     * @param otherParticipant 메시지를 보낸 사용자를 제외한 참여자
     * @throws IllegalArgumentException 메시지를 보낸 참여자가 메시지를 읽음
     */
    public void read(Participant otherParticipant) {

        if(otherParticipant.equals(this.participant)) {
            throw new IllegalArgumentException("메시지를 보낸 사용자입니다. userId = " + otherParticipant.getId());
        }

        MessageRead messageRead = MessageRead.of(otherParticipant, this.room, this);
        this.messageReads.add(messageRead);
    }

    /**
     * 기존 메시지에 코멘트 달기
     * @param message 자식 메시지
     */
    public void comment(Message message) {
        this.child_messages.add(message);
        message.parent_message = this;
    }

    /**
     * 메시지와 메시지파일을 매핑하는 함수
     * @param filePath 파일경로
     * @param fileName 파일명
     */
    public void mappingFile(String filePath, String fileName) {
        this.messageFile = MessageFile.of(this, filePath, fileName);
    }

    /**
     * 메시지 삭제
     */
    public void delete() {
        this.isDelete = true;
    }

    protected enum MessageType {
        TEXT,
        IMAGE
    }
}
