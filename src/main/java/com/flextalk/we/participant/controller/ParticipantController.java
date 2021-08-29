package com.flextalk.we.participant.controller;

import com.flextalk.we.cmmn.response.SuccessResponse;
import com.flextalk.we.participant.dto.ParticipantPromoteRequestDto;
import com.flextalk.we.participant.dto.ParticipantResponseDto;
import com.flextalk.we.participant.dto.ParticipantSaveRequestDto;
import com.flextalk.we.participant.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping(value = "/v1/rooms/{roomId}/participants")
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<List<ParticipantResponseDto>> getParticipantsByRoom(@PathVariable Long roomId) {
        return SuccessResponse.of(HttpStatus.OK.value(), participantService.getParticipantsByRoom(roomId));
    }

    @PostMapping(value = "/v1/rooms/{roomId}/participants")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> inviteParticipants(@PathVariable Long roomId,
                                                    @RequestBody @Valid ParticipantSaveRequestDto participantSaveRequestDto) {

        return SuccessResponse.of(HttpStatus.CREATED.value(), participantService.inviteParticipants(roomId, participantSaveRequestDto.getUserIds()));
    }

    @DeleteMapping(value = "/v1/rooms/{roomId}/participants/{participantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse<Long> leaveParticipant(@PathVariable Long roomId,
                                                  @PathVariable Long participantId) {

        return SuccessResponse.of(HttpStatus.NO_CONTENT.value(), participantService.leaveParticipant(roomId, participantId));
    }

    @DeleteMapping(value = "/v1/rooms/{roomId}/participants/owner-participant/{ownerParticipantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse<Long> deportParticipants(@PathVariable Long roomId,
                                                    @PathVariable Long ownerParticipantId,
                                                    @RequestParam String deportParticipantIds) {

        return SuccessResponse.of(HttpStatus.NO_CONTENT.value(), participantService.deportParticipants(roomId, ownerParticipantId, deportParticipantIds));
    }

    @PutMapping(value = "/v1/rooms/{roomId}/participants/owner-participant/{ownerParticipantId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> promoteParticipant(@PathVariable Long roomId,
                                                    @PathVariable Long ownerParticipantId,
                                                    @RequestBody @Valid ParticipantPromoteRequestDto participantPromoteRequestDto) {

        return SuccessResponse.of(HttpStatus.CREATED.value(), participantService.promotePermission(roomId, ownerParticipantId, participantPromoteRequestDto));
    }

    @PostMapping(value = "/v1/rooms/{roomId}/participants/{participantId}/bookmark")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> addBookMarkToParticipant(@PathVariable Long roomId,
                                                          @PathVariable Long participantId) {

        return SuccessResponse.of(HttpStatus.CHECKPOINT.value(), participantService.addBookMarkToParticipant(participantId, roomId));
    }

    @DeleteMapping(value = "/v1/rooms/{roomId}/participants/{participantId}/bookmark")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse<Long> deleteBookMarkToParticipant(@PathVariable Long roomId,
                                                             @PathVariable Long participantId) {

        return SuccessResponse.of(HttpStatus.NO_CONTENT.value() ,participantService.deleteBookMarkToParticipant(participantId, roomId));
    }

    @PostMapping(value = "/v1/rooms/{roomId}/participants/{participantId}/alarm")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> addAlarmToParticipant(@PathVariable Long roomId,
                                                       @PathVariable Long participantId) {

        return SuccessResponse.of(HttpStatus.CREATED.value(), participantService.addAlarmToParticipant(participantId, roomId));
    }

    @DeleteMapping(value = "/v1/rooms/{roomId}/participants/{participantId}/alarm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse<Long> deleteAlarmToParticipant(@PathVariable Long roomId,
                                                          @PathVariable Long participantId) {

        return SuccessResponse.of(HttpStatus.NO_CONTENT.value(), participantService.deleteAlarmToParticipant(participantId, roomId));
    }
}
