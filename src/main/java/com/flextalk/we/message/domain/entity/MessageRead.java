package com.flextalk.we.message.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.participant.domain.entity.Participant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_message_read", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"message_id", "participant_id"})
})
public class MessageRead extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_read_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    /**
     * 메시지 읽음 생성자
     * @param participant 메시지 읽은 참여자
     * @param message 메시지
     */
    private MessageRead(Participant participant, Message message) {
        this.participant = Objects.requireNonNull(participant);
        this.message = Objects.requireNonNull(message);
    }

    /**
     * 메시지 읽음 생성 함수
     * @param participant 메시지 읽은 참여자
     * @param message 메시지
     * @return 메시지 읽음 객체
     */
    public static MessageRead of(Participant participant, Message message) {
        MessageRead messageRead = new MessageRead(participant, message);
        return messageRead;
    }
}
