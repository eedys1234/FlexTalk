package com.flextalk.we.participant.cmmn;

import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.room.domain.entity.Room;
import com.flextalk.we.user.domain.entity.User;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ParticipantMatcher {

    public static Participant matchingRoomOwner(Room room) {
        return room.participants().stream()
                .filter(Participant::getIsOwner)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));
    }

    public static List<Participant> matchingNotRoomOwner(Room room) {
        return room.participants().stream()
                .filter(part -> !part.getIsOwner())
                .collect(toList());
    }

    public static Participant matchingParticipant(Room room, Participant otherParticipant) {
        return room.participants().stream()
                .filter(participant -> participant.equals(otherParticipant))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));
    }

    public static Participant matchingParticipant(Room room, User otherUser) {
        return room.participants().stream()
                .filter(participant -> participant.isParticipant(otherUser))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));
    }
}
