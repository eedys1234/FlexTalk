package com.flextalk.we.user.controller;

import com.flextalk.we.cmmn.auth.ApiPermission;
import com.flextalk.we.cmmn.response.SuccessResponse;
import com.flextalk.we.user.domain.entity.Role;
import com.flextalk.we.user.dto.*;
import com.flextalk.we.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiPermission(target = Role.ROLE_GUEST)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/v1/user")
    public SuccessResponse<Long> register(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.register(userRegisterDto));
    }

    @ApiPermission(target = Role.ROLE_GUEST)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/v1/user/login")
    public SuccessResponse<Long> login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
        return SuccessResponse.of(HttpStatus.OK.value(), userService.loadUserByUsername(userLoginRequestDto.getUserEmail()));
    }

    @PostMapping(value = "/v1/user/info")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> update(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.update(userUpdateDto));
    }

    @ApiPermission(target = Role.ROLE_ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping(value = "/v1/user/grant-authority")
    public SuccessResponse<Long> grantAuthority(@RequestBody @Valid UserRoleGrantDto userRoleGrantDto) {
        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.grantAuthority(userRoleGrantDto));
    }

    @ApiPermission(target = Role.ROLE_ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping(value = "/v1/user/loss-authority")
    public SuccessResponse<Long> lossAuthority(@RequestBody @Valid UserRoleGrantDto userRoleGrantDto) {
        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.lossAuthority(userRoleGrantDto));
    }

    @ApiPermission(target = Role.ROLE_ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping(value = "/v1/user/approve")
    public SuccessResponse<Long> approve(@RequestBody @Valid UserApproveDto userApproveDto) {
        return SuccessResponse.of(HttpStatus.CREATED.value(), userService.approve(userApproveDto));
    }

}
