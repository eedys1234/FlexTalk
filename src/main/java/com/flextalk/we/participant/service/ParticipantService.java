package com.flextalk.we.participant.service;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.cmmn.exception.ResourceAccessDeniedException;
import com.flextalk.we.participant.dto.ParticipantResponseDto;
import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.participant.repository.repository.ParticipantRepository;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.service.RoomService;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final RoomService roomService;
    private final UserService userService;

    /**
     * 채팅방 참여자 리스트 
     * @param roomId 채팅방 ID
     * @return 채팅방 참여자 리스트
     * @throws NotEntityException 채팅방에 존재하지 않을경우
     */
    @Transactional(readOnly = true)
    public List<ParticipantResponseDto> getParticipantsByRoom(Long roomId) {

        final Room room = roomService.findRoom(roomId);

        return participantRepository.findByRoom(room).stream()
                .map(ParticipantResponseDto::new)
                .collect(toList());
    }

    /**
     * 채팅방에 사용자 초대
     * @param roomId 채팅방 ID
     * @param userIds 초대되는 사용자들의 ID
     * @return 채팅방에 참여된 참여자들의 ID
     */
    @Transactional
    public List<Long> inviteParticipants(Long roomId, String userIds) {

        final String[] splitUserIds = userIds.split(",");
        final List<User> users = userService.findUsers(splitUserIds);

        userService.findMatchingUsers(users, splitUserIds);

        final Room room = roomService.findRoomAddedAddiction(roomId);
        
        return room.invite(users);
    }


    /**
     * 채팅방에 참여자가 나가기
     * @param roomId 참여자가 나가려는 채팅방 ID
     * @param participantId 나가는 참여자 ID
     * @return 나가는 참여자 ID
     */
    @Transactional
    public Long leaveParticipant(Long roomId, Long participantId) {

        final Participant participant = findParticipant(participantId);

        final Room room = roomService.findRoomAddedAddiction(roomId);

        return room.leave(participant);
    }

    /**
     * 참여자 추방하기
     * @param roomId 채팅방 ID
     * @param ownerParticipantId 권한을 가진 참여자 ID
     * @param deportParticipantIds 추방되는 참여자들의 ID
     * @throws NotEntityException 권한을 가진 참여자나 권한을 받을 참여자가 존재하지 않을경우
     * @throws ResourceAccessDeniedException 권한을 가진 참여자가 권한이 없을경우
     * @return 추방된 참여자 ID
     */
    @Transactional
    public List<Long> deportParticipants(Long roomId, Long ownerParticipantId, String deportParticipantIds) {

        String[] splitDeportParticipantIds = deportParticipantIds.split(",");
        List<Long> ids = Arrays.stream(splitDeportParticipantIds).map(id -> Long.parseLong(id)).collect(toList());

        List<Participant> deportParticipants = participantRepository.findByIds(ids);

        Participant roomOwner = participantRepository.findOwner(ownerParticipantId)
                .orElseThrow(() -> new NotEntityException("채팅방 방장이 존재하지 않습니다. participantId = " + ownerParticipantId));

        final Room room = roomService.findRoomAddedAddiction(roomId);

        if(!roomOwner.getIsOwner()) {
            throw new ResourceAccessDeniedException("권한이 존재하지 않습니다.");
        }

        return room.leave(deportParticipants);
    }

    /**
     * 채팅방 권한을 넘기다
     * @param ownerParticipantId 권한을 가진 참여자 ID
     * @param promoteParticipantId 권한을 받는 참여자 ID
     * @throws NotEntityException 권한을 가진 참여자나 권한을 받을 참여자가 존재하지 않을경우
     * @throws ResourceAccessDeniedException 권한을 가진 참여자가 권한이 없을경우
     * @return 권한을 받는 참여자 ID
     */
    public Long promotePermission(Long ownerParticipantId, Long promoteParticipantId) {

        Participant promoteParticipant = participantRepository.findOne(promoteParticipantId)
                .orElseThrow(() -> new NotEntityException("추방 당하려는 참여자가 존재하지 않습니다. participantId = " + promoteParticipantId));

        Participant roomOwnerParticipant = participantRepository.findOwner(ownerParticipantId)
                .orElseThrow(() -> new NotEntityException("채팅방 방장이 존재하지 않습니다. participantId = " + ownerParticipantId));

        if(!roomOwnerParticipant.getIsOwner()) {
            throw new ResourceAccessDeniedException("권한이 존재하지 않습니다.");
        }

        promoteParticipant.assignOwner();
        roomOwnerParticipant.resign();

        return promoteParticipant.getId();
    }

    /**
     * 즐겨찾기 등록
     * @param participantId 즐겨찾기를 등록하려는 참여자 ID
     * @param roomId 즐겨찾기를 등록하려는 채팅방 ID
     * @return 즐겨찾기가 등록된 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(참여자 | 채팅방)
     * @throws IllegalStateException 채팅방에 참여자가 존재하지 않을경우
     * @throws IllegalArgumentException 이미 즐겨찾기가 등록되어있거나 채팅방의 참여자가 아닐경우
     */
    @Transactional
    public Long addBookMarkToRoom(Long participantId, Long roomId) {

        final Participant participant = findParticipant(participantId);

        final Room room = roomService.findRoomAddedAddiction(roomId);

        room.addBookMark(participant);
        return room.getId();
    }

    /**
     * 즐겨찾기 삭제
     * @param participantId 즐겨찾기를 삭제하려는 참여자 ID
     * @param roomId 즐겨찾기가 등록된 채팅방 ID
     * @return 즐겨찾기가 삭제된 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(참여자 | 채팅방)
     * @throws IllegalArgumentException 삭제하려는 채팅방에 삭제하려는 즐겨찾기가 없을경우
     */
    @Transactional
    public Long deleteBookMarkToRoom(Long participantId, Long roomId) {

        final Participant participant = findParticipant(participantId);

        final Room room = roomService.findRoomAddedAddiction(roomId);

        room.deleteBookMark(participant);
        return room.getId();
    }

    /**
     * 알람 설정
     * @param participantId 알람을 설정하려는 참여자 ID
     * @param roomId 알람을 설정하려는 채팅방 ID
     * @return 알람이 설정된 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(참여자 | 채팅방)
     * @throws IllegalStateException 채팅방에 참여자가 존재하지 않을경우
     * @throws IllegalArgumentException 이미 알람이 설정되어있거나 채팅방의 참여자가 아닐경우
     */
    @Transactional
    public Long addAlarmToRoom(Long participantId, Long roomId) {

        final Participant participant = findParticipant(participantId);

        final Room room = roomService.findRoomAddedAddiction(roomId);

        room.addAlarm(participant);
        return room.getId();
    }

    /**
     * 알람 삭제
     * @param participantId 알람을 삭제하려는 참여자 ID
     * @param roomId 알람이 설정된 채팅방 ID
     * @return 알람이 삭제된 채팅방 ID
     * @throws NotEntityException 요청된 정보가 존재하지 않을경우(참여자 | 채팅방)
     * @throws IllegalArgumentException 알람이 설정되지 않을경우
     */
    @Transactional
    public Long deleteAlarmToRoom(Long participantId, Long roomId) {

        final Participant participant = findParticipant(participantId);

        final Room room = roomService.findRoomAddedAddiction(roomId);

        room.deleteAlarm(participant);
        return room.getId();
    }

    @Transactional(readOnly = true)
    public Participant findParticipant(Long participantId) {
        return participantRepository.findOne(participantId)
                .orElseThrow(() -> new NotEntityException("참여자가 존재하지 않습니다. participantId = " + participantId));
    }
}
