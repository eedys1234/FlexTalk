package com.flextalk.we.message.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_message_read")
public class MessageRead extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_read_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 메시지 읽음 생성자
     * @param user 메시지 읽은 사용자
     * @param room 메시지가 존재하는 채팅방
     * @param message 메시지
     */
    private MessageRead(User user, Room room, Message message) {
        this.user = Objects.requireNonNull(user);
        this.room = Objects.requireNonNull(room);
        this.message = Objects.requireNonNull(message);
    }

    /**
     * 메시지 읽음 생성 함수
     * @param user 메시지 읽은 사용자
     * @param room 메시지가 존재하는 채팅방
     * @param message 메시지
     * @return 메시지 읽음 객체
     */
    public static MessageRead of(User user, Room room, Message message) {
        MessageRead messageRead = new MessageRead(user, room, message);
        return messageRead;
    }
}
