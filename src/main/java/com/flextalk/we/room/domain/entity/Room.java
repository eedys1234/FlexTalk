package com.flextalk.we.room.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.user.domain.entity.User;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_room")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "room_name", length = 150, nullable = false)
    private String roomName;

    @Embedded private RoomTypeInfo roomTypeInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomBookMark> roomBookMarks = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomAlarm> roomAlarms = new ArrayList<>();

    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL)
    private RoomMessageDate roomMessageDate;

    @Column(name = "is_delete")
    private Boolean isDelete;

    private Room(String roomName, String roomType, Integer roomLimitCount) {
        this.roomName = Objects.requireNonNull(roomName);
        this.roomTypeInfo = new RoomTypeInfo(roomType, roomLimitCount);
    }

    /**
     * 채팅방 생성 함수
     * @param creator 채팅방 생성자
     * @param roomName 채팅방 이름
     * @param roomType 채팅방 종류
     * @param roomLimitCount 채팅방 인원
     * @return 채팅방
     */
    public static Room create(User creator, String roomName, String roomType, Integer roomLimitCount) {
        Room room = new Room(roomName, roomType, roomLimitCount);
        room.appoint(creator);
        return room;
    }

    /**
     * 채팅방 생성자를 임명
     * @param creator 채팅방 생성자
     */
    private void appoint(User creator) {
        this.creator = creator;
        invite(creator);
    }

    /**
     * 채팅방 삭제
     * @param user 채팅방 Owner
     */
    public void delete(User user) {
        if(canDelete(user)) {
            this.isDelete = true;
        }
    }

    /**
     * 채팅방 삭제 가능한지 확인 함수
     * @return 채팅방 삭제 가능여부
     */
    public boolean canDelete(User user) {
        if(!matchParticipant(user)) {
            return false;
        }
        return this.participants.size() == 1;
    }

    /**
     * 채팅방에 사용자를 초대하다
     * @param user 참여자
     * @return 참여자 ID
     * @throws IllegalStateException 채팅방을 생성할 때 제한된 인원에 도달했을 경우
     * @throws IllegalArgumentException 초대하고자하는 사용자가 이미 채팅방에 참여했을 경우
     */
    public Long invite(User user) {

        if(isFullParticipant()) {
            throw new IllegalStateException("채팅방 제한인원이 도달하였습니다.");
        }

        if(matchParticipant(user)) {
            throw new IllegalArgumentException("이미 채팅방에 참여하였습니다. userId = " + user.getId());
        }

        Participant participant = Participant.of(this, user);
        this.participants.add(participant);
        return participant.getId();
    }

    /**
     * 채팅방에 사용자를 초대하다
     * @param users 초대하려는 사용자 리스트
     * @return 참여자 ID
     * @throws IllegalStateException 채팅방을 생성할 때 제한된 인원에 도달했을 경우
     * @throws IllegalArgumentException 초대하고자하는 사용자 중 일부 사용자가 이미 채팅방에 참여했을 경우
     */
    public List<Long> invite(List<User> users) {
        List<Long> participantIds = new ArrayList<>();
        
        for(User user : users) {
            participantIds.add(invite(user));
        }
        return participantIds;
    }
    
    /**
     * 사용자가 채팅방에 떠나다
     * @param leaveParticipant 채팅방을 떠나고자 하는 참여자
     * @throws IllegalStateException 채팅방 인원이 존재하지 않을경우
     * @throws IllegalArgumentException 떠나고자 하는 참여자가 채팅방에 참여자가 아닐경우
     */
    public void leave(Participant leaveParticipant) {

        if (isEmptyParticipant()) {
            throw new IllegalStateException("채팅방에는 최소 1명의 참여자가 존재해야합니다. roomId = " + this.id);
        }

        if (!matchParticipant(leaveParticipant)) {
            throw new IllegalArgumentException("채팅방의 참여자가 아닙니다. participantId = " + leaveParticipant.getId());
        }

        this.participants = this.participants.stream()
                .filter(participant -> !participant.equals(leaveParticipant))
                .collect(toList());
    }

    /**
     * 채팅방의 참여자를 가져오다
     * @return 채팅방 참여자 목록
     */
    public List<Participant> participants() {
        return this.participants;
    }

    /**
     * 참여자가 존재하는지 확인
     * @return 참여자가 존재하는지 여부
     */
    private boolean isEmptyParticipant() {
        return this.participants.isEmpty();
    }

    /**
     * 채팅방의 참여자 수가 제한된 수만큼 찼는지 확인
     * @return 참여자수가 제한된 수와 동일한지 여부
     */
    private boolean isFullParticipant() {
        return this.participants.size() == this.roomTypeInfo.getRoomLimitCount();
    }

    /**
     * 채팅방의 참여자와 일치하는 참여자 여부
     * @param user 참여자
     * @return 참여 여부
     * @throws IllegalArgumentException 채팅방에 참여자가 아닐경우
     */
    private boolean matchParticipant(User user) {
        return this.participants.stream()
                .anyMatch(participant -> participant.isParticipant(user));
    }

    /**
     * 채팅방의 참여자와 일치하는 참여자 여부
     * @param otherParticipant 참여자
     * @return 참여 여부
     * @throws IllegalArgumentException 채팅방에 참여자가 아닐경우
     */
    private boolean matchParticipant(Participant otherParticipant) {
        return this.participants.stream()
                .anyMatch(participant -> participant.equals(otherParticipant));
    }

    /**
     * 채팅방 즐겨찾기 등록
     * @param user 즐겨찾기 하는 참여자
     * @throws IllegalStateException 채팅방에 참여자가 한명도 없을 시
     */
    public void addBookMark(User user) {

        if(isEmptyParticipant()) {
            //인수값이 무엇이든지 실패할경우
            throw new IllegalStateException("채팅방에는 최소 1명의 참여자가 존재해야합니다. roomId = " + this.id);
        }

        if(!matchParticipant(user)) {
            throw new IllegalArgumentException("채팅방의 참여자가 아닙니다. userId = " + user.getId());
        }

        if(matchBookMark(user)) {
            throw new IllegalArgumentException("이미 즐겨찾기를 등록하였습니다. userId = " + user.getId());
        }

        RoomBookMark bookMark = RoomBookMark.of(user, this);
        this.roomBookMarks.add(bookMark);
    }

    /**
     * 즐겨찾기 삭제
     * @param user 즐겨찾기 삭제하려는 사용자
     * @throws IllegalArgumentException 즐겨찾기가 등록되지 않을경우
     */
    public void deleteBookMark(User user) {
        if(!matchBookMark(user)) {
            throw new IllegalArgumentException("채팅방에 즐겨찾기가 등록되지 않았습니다. roomId = " + this.id);
        }

        this.roomBookMarks = this.roomBookMarks.stream()
                .filter(book -> !book.getUser().equals(user))
                .collect(toList());
    }

    /**
     * room bookmark matching
     * @param user 즐겨찾기를 설정하려는 사람
     * @return 즐겨찾기 설정되었는지 여부
     */
    private boolean matchBookMark(User user) {
        return this.roomBookMarks.stream().anyMatch(book -> book.getUser().equals(user));
    }

    /**
     * 채팅방에 알람설정
     * @param user 알람설정 하는 참여자
     * @throws IllegalStateException 채팅방에 참여자가 한명도 없을 시
     */
    public void addAlarm(User user) {

        if(isEmptyParticipant()) {
            //인수값이 무엇이든지 실패할경우
            throw new IllegalStateException("채팅방에는 최소 1명의 참여자가 존재해야합니다. roomId = " + this.id);
        }

        if(!matchParticipant(user)) {
            throw new IllegalArgumentException("채팅방의 참여자가 아닙니다. userId = " + user.getId());
        }
        
        if(matchAlarm(user)) {
            throw new IllegalArgumentException("이미 알람을 설정하였습니다. userId = " + user.getId());
        }

        RoomAlarm alarm = RoomAlarm.of(user, this);
        this.roomAlarms.add(alarm);
    }

    /**
     * 알람 삭제
     * @param user 알람을 삭제하려는 사용자
     * @throws IllegalArgumentException 알람이 설정되지 않을경우
     */
    public void deleteAlarm(User user) {

        if(!matchAlarm(user)) {
            throw new IllegalArgumentException("채팅방에 알람이 설정되지 않았습니다. roomId = " + this.id);
        }

        this.roomAlarms = this.roomAlarms.stream()
                .filter(alarm -> !alarm.getUser().equals(user))
                .collect(toList());

    }

    /**
     * room alarm matching
     * @param user 알람을 설정하려는 사람
     * @return 알람이 설정되었는지 여부
     */
    private boolean matchAlarm(User user) {
        return this.roomAlarms.stream().anyMatch(alarm -> alarm.getUser().equals(user));
    }

    /**
     * 채팅방 내 최근 메시지의 시간 업데이트
     */
    public void updateRecentDate() {
        this.roomMessageDate = RoomMessageDate.generate(this);
    }

}
