package com.flextalk.we.participant.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ParticipantPromoteRequestDto {

    @NotNull
    private Long promoteParticipantId;

}
