package com.flextalk.we.participant.service;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.cmmn.exception.ResourceAccessDeniedException;
import com.flextalk.we.participant.dto.ParticipantResponseDto;
import com.flextalk.we.participant.repository.entity.Participant;
import com.flextalk.we.participant.repository.repository.ParticipantRepository;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.room.domain.repository.RoomRepository;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
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
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    /**
     * 채팅방 참여자 리스트 
     * @param roomId 채팅방 ID
     * @return 채팅방 참여자 리스트
     * @throws NotEntityException 채팅방에 존재하지 않을경우
     */
    @Transactional(readOnly = true)
    public List<ParticipantResponseDto> getParticipantsByRoom(Long roomId) {

        final Room room = roomRepository.findOne(roomId)
            .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));

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

        /**
         * 다음 로직이 과연 UserService가 아닌 ParticipantService의 책임인가?
         * 고민 1) Participant Service에서 UserService, RoomService 호출
         * 고민 2) Participant Controller에서 호출
         */
        String[] splitUserIds = userIds.split(",");

        List<User> users = userRepository.findByIds(Arrays.stream(splitUserIds)
                .map(id -> Long.parseLong(id))
                .collect(toList()));

        for(String id : splitUserIds)
        {
            if(users.stream().noneMatch(user -> id.equals(String.valueOf(user.getId())))) {
                throw new NotEntityException("사용자가 존재하지 않습니다. userId = " + id);
            }
        }

        Room room = roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));
        
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

        Participant participant = participantRepository.findOne(participantId)
                .orElseThrow(() -> new NotEntityException("참여자가 존재하지 않습니다. participantId = " + participantId));

        Room room = roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));

        room.leave(participant);
        return participant.getId();
    }

    /**
     * 참여자 추방하기
     * @param roomId
     * @param ownerParticipantId
     * @param deportParticipantId
     * @return
     */
    @Transactional
    public Long deportParticipant(Long roomId, Long ownerParticipantId, Long deportParticipantId) {

        Participant deportParticipant = participantRepository.findOne(deportParticipantId)
                .orElseThrow(() -> new NotEntityException("추방 당하려는 참여자가 존재하지 않습니다. participantId = " + deportParticipantId));

        Participant roomOwnerParticipant = participantRepository.findOne(ownerParticipantId)
                .orElseThrow(() -> new NotEntityException("채팅방 방장이 존재하지 않습니다. participantId = " + ownerParticipantId));

        Room room = roomRepository.findOneWithDetailInfo(roomId)
                .orElseThrow(() -> new NotEntityException("채팅방이 존재하지 않습니다. roomId = " + roomId));

        if(!roomOwnerParticipant.getIsOwner()) {
            throw new ResourceAccessDeniedException("권한이 존재하지 않습니다.");
        }

        room.leave(deportParticipant);
        return deportParticipant.getId();
    }

    /**
     * 채팅방 권한을 넘기다
     * @param ownerParticipantId
     * @param promoteParticipantId
     * @return
     */
    public Long promotePermission(Long ownerParticipantId, Long promoteParticipantId) {

        Participant promoteParticipant = participantRepository.findOne(promoteParticipantId)
                .orElseThrow(() -> new NotEntityException("추방 당하려는 참여자가 존재하지 않습니다. participantId = " + promoteParticipantId));

        Participant roomOwnerParticipant = participantRepository.findOne(ownerParticipantId)
                .orElseThrow(() -> new NotEntityException("채팅방 방장이 존재하지 않습니다. participantId = " + ownerParticipantId));

        if(!roomOwnerParticipant.getIsOwner()) {
            throw new ResourceAccessDeniedException("권한이 존재하지 않습니다.");
        }

        promoteParticipant.assignOwner();
        roomOwnerParticipant.resign();

        return promoteParticipant.getId();
    }
}
