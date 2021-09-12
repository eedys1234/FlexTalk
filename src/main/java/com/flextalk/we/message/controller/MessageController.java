package com.flextalk.we.message.controller;

import com.flextalk.we.cmmn.response.SuccessResponse;
import com.flextalk.we.message.dto.MessageReadUpdateDto;
import com.flextalk.we.message.dto.MessageSaveRequestDto;
import com.flextalk.we.message.dto.MessageUnReadResponseDto;
import com.flextalk.we.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class MessageController {

    private final MessageService messageService;

    @PostMapping(value = "/v1/rooms/{roomId}/participants/{participantId}/messages/text")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> sendMessage(@PathVariable Long roomId,
                                             @PathVariable Long participantId,
                                             @RequestBody @Valid MessageSaveRequestDto messageSaveRequestDto) {

        Long sendMessageId = messageService.sendTextMessage(roomId, participantId, messageSaveRequestDto);
        return SuccessResponse.of(HttpStatus.CREATED.value(), sendMessageId);
    }

    @PostMapping(value = "/v1/rooms/{roomId}/participants/{participantId}/messages/file")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> sendFileMessage(@PathVariable Long roomId,
                                                 @PathVariable Long participantId,
                                                 @RequestBody @Valid MessageSaveRequestDto messageSaveRequestDto,
                                                 MultipartHttpServletRequest multipartHttpServletRequest) {

        MultipartFile multipartFile = Optional.ofNullable(multipartHttpServletRequest)
                .map(multipart -> multipart.getFile("file"))
                .orElse(null);

        Long sendMessageId = null;

        try {
            byte[] bytes = multipartFile.getBytes();
            sendMessageId = messageService.sendFileMessage(roomId, participantId, messageSaveRequestDto, multipartFile.getOriginalFilename(), bytes);
        }
        catch (IOException e) {
            log.error("메시지 파일이 존재하지 않습니다.");
            log.error(e.getMessage());
        }

        return SuccessResponse.of(HttpStatus.CREATED.value(), sendMessageId);
    }

    @DeleteMapping(value = "/v1/rooms/{roomId}/participants/{participantId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse<Long> deleteMessage(@PathVariable Long roomId,
                                               @PathVariable Long participantId,
                                               @PathVariable Long messageId) {

        return SuccessResponse.of(HttpStatus.NO_CONTENT.value(), messageService.deleteMessage(messageId, participantId, roomId));
    }

    @GetMapping(value = "/v1/rooms/{roomId}/unread-messages")
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<List<MessageUnReadResponseDto>> unReadMessageCount(@PathVariable Long roomId,
                                                                              @RequestParam String messageIds) {

        return SuccessResponse.of(HttpStatus.OK.value(), messageService.unReadMessagesCount(roomId, messageIds));
    }

    @PutMapping(value = "/v1/messages/read")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> readMessages(@RequestBody @Valid MessageReadUpdateDto messageReadUpdateDto) {

        return SuccessResponse.of(HttpStatus.CREATED.value(), messageService.readMessage(messageReadUpdateDto));
    }
}
