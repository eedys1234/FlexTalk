package com.flextalk.we.participant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flextalk.we.participant.domain.entity.Participant;
import com.flextalk.we.user.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
public class ParticipantResponseDto {

    @JsonProperty(value = "participant_id")
    private Long participantId;

    @JsonProperty(value = "user_email")
    private String userEmail;

    @JsonProperty(value = "user_profile")
    private String userProfile;

    public ParticipantResponseDto(Participant participant) {
        User user = participant.getUser();
        this.participantId = participant.getId();

        if(Objects.nonNull(user)) {
            this.userEmail = user.getEmail();
            this.userProfile = user.getProfile();
        }
    }
}
