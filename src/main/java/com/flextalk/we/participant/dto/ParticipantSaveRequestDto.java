package com.flextalk.we.participant.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ParticipantSaveRequestDto {

    @NotBlank
    private String userIds;

}
