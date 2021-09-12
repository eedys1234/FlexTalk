package com.flextalk.we.user.controller;

import com.flextalk.we.cmmn.response.SuccessResponse;
import com.flextalk.we.user.dto.*;
import com.flextalk.we.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/v1/user")
    public SuccessResponse<Long> register(UserRegisterDto userRegisterDto) {
        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.register(userRegisterDto));
    }

    @PostMapping(value = "/v1/user/login")
    public SuccessResponse<Long> login(UserLoginRequestDto userLoginRequestDto) {
        return SuccessResponse.of(HttpStatus.OK.value(), userService.loadUserByUsername(userLoginRequestDto.getUserEmail()));
    }

    @PostMapping(value = "/v1/user/info")
    public SuccessResponse<Long> update(UserUpdateDto userUpdateDto,
                                        MultipartHttpServletRequest multipartHttpServletRequest) {

        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.update(userUpdateDto));
    }

    @PutMapping(value = "/v1/user/grant-authority")
    public SuccessResponse<Long> grantAuthority(UserRoleGrantDto userRoleGrantDto) {
        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.grantAuthority(userRoleGrantDto));
    }

    @PutMapping(value = "/v1/user/loss-authority")
    public SuccessResponse<Long> lossAuthority(UserRoleGrantDto userRoleGrantDto) {
        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.lossAuthority(userRoleGrantDto));
    }

    @PutMapping(value = "/v1/user/approve")
    public SuccessResponse<Long> approve(UserApproveDto userApproveDto) {
        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.approve(userApproveDto));
    }

}
