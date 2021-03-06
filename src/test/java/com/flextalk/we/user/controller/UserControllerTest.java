package com.flextalk.we.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextalk.we.cmmn.token.jwt.JWTSecurityKey;
import com.flextalk.we.cmmn.token.jwt.JWTTokenGenerator;
import com.flextalk.we.cmmn.token.jwt.JWTTokenGeneratorTest;
import com.flextalk.we.cmmn.token.TokenGenerator;
import com.flextalk.we.cmmn.util.AuthConstants;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.entity.Role;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.TokenRepository;
import com.flextalk.we.user.dto.UserApproveDto;
import com.flextalk.we.user.dto.UserLoginRequestDto;
import com.flextalk.we.user.dto.UserRegisterDto;
import com.flextalk.we.user.dto.UserRoleGrantDto;
import com.flextalk.we.user.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;
import java.util.Collections;

import static com.flextalk.we.user.cmmn.MockUserInfo.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(value = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JWTSecurityKey jwtSecurityKey;

    @MockBean
    private TokenGenerator<CustomUser> jwtTokenGenerator;

    @MockBean
    private TokenRepository tokenRepository;

    private String token;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        JWTTokenGenerator jwtTokenGenerator = new JWTTokenGenerator(jwtSecurityKey);
        objectMapper = new ObjectMapper();

        long adminUserId = 1L;

        MockUserFactory mockUserFactory = new MockUserFactory();
        User adminUser = mockUserFactory.create(ADMIN_EMAIL, ADMIN_PASSWORD);
        adminUser.grantAuthority(Role.ROLE_ADMIN);
        ReflectionTestUtils.setField(adminUser, "id", adminUserId);

        String base64SecurityKey = Base64.getEncoder().encodeToString("123##2da".getBytes());
        doReturn(base64SecurityKey).when(jwtSecurityKey).getBaseSecurityKey();
        CustomUser customUser = new CustomUser(adminUser, Collections.singleton(new SimpleGrantedAuthority(adminUser.getRole().getKey())));
        token = jwtTokenGenerator.generate(customUser);
    }

    @DisplayName("????????? ?????? ?????????")
    @Test
    public void userRegisterTest() throws Exception {

        //given
        final String url = "/api/v1/user";
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        ReflectionTestUtils.setField(userRegisterDto, "userEmail", NORMAL_EMAIL);
        ReflectionTestUtils.setField(userRegisterDto, "userPassword", NORMAL_PASSWORD);
        long userId = 1L;

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDto)));

        doReturn(userId).when(userService).register(any(UserRegisterDto.class));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        //verify
        verify(userService, times(1)).register(any(UserRegisterDto.class));
    }

    @DisplayName("????????? ?????? Email ?????? ?????? ????????? - xxx@xxx.*")
    @Test
    public void userRegisterEmailValidationTest() throws Exception {

        //given
        final String url = "/api/v1/user";
        String failureEmail = "test";
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        ReflectionTestUtils.setField(userRegisterDto, "userEmail", failureEmail);
        ReflectionTestUtils.setField(userRegisterDto, "userPassword", NORMAL_PASSWORD);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        //verify
        verify(userService, times(0)).register(any(UserRegisterDto.class));

    }

    @DisplayName("????????? ?????? Password ?????? ?????? ????????? - 8 ~ 12??????")
    @Test
    public void userRegisterPasswordValidationTest() throws Exception {

        //given
        final String url = "/api/v1/user";
        String failurePassword = "ttt1234";
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        ReflectionTestUtils.setField(userRegisterDto, "userEmail", NORMAL_EMAIL);
        ReflectionTestUtils.setField(userRegisterDto, "userPassword", failurePassword);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(userService, times(0)).register(any(UserRegisterDto.class));
    }

    @DisplayName("????????? approve ?????????")
    @Test
    public void userApproveTest() throws Exception {

        //given
        long userId = 2L;

        final String url = "/api/v1/user/approve";
        UserApproveDto userApproveDto = new UserApproveDto();
        ReflectionTestUtils.setField(userApproveDto, "userId", userId);

        doReturn(userId).when(userService).approve(any(UserApproveDto.class));
        doReturn(token).when(jwtTokenGenerator).getTokenFromHeader(anyString());
        doReturn(true).when(jwtTokenGenerator).isValidateToken(anyString());
        doReturn("ROLE_ADMIN").when(jwtTokenGenerator).getRoleFromToken(anyString());
        doReturn(true).when(tokenRepository).findToken(anyString());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .header(AuthConstants.AUTH_HEADER, String.format("%s %s", AuthConstants.TOKEN_TYPE, token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userApproveDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        //verify
        verify(userService, times(1)).approve(any(UserApproveDto.class));
    }

    @DisplayName("????????? ?????? ?????? ?????????")
    @Test
    public void userGrantAuthorityTest() throws Exception {

        //given
        long userId = 2L;
        final String url = "/api/v1/user/grant-authority";
        UserRoleGrantDto userRoleGrantDto = new UserRoleGrantDto();
        ReflectionTestUtils.setField(userRoleGrantDto, "userGrantRole", Role.ROLE_ADMIN.getKey());
        ReflectionTestUtils.setField(userRoleGrantDto, "userId", userId);

        doReturn(userId).when(userService).grantAuthority(any(UserRoleGrantDto.class));
        doReturn(userId).when(userService).approve(any(UserApproveDto.class));
        doReturn(token).when(jwtTokenGenerator).getTokenFromHeader(anyString());
        doReturn(true).when(jwtTokenGenerator).isValidateToken(anyString());
        doReturn("ROLE_ADMIN").when(jwtTokenGenerator).getRoleFromToken(anyString());
        doReturn(true).when(tokenRepository).findToken(anyString());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .header(AuthConstants.AUTH_HEADER, String.format("%s %s", AuthConstants.TOKEN_TYPE, token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRoleGrantDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        //verify
        verify(userService, times(1)).grantAuthority(any(UserRoleGrantDto.class));
    }

    @DisplayName("????????? ?????? ?????? ?????????")
    @Test
    public void userLossAuthorityTest() throws Exception {

        //given
        long userId = 2L;
        final String url = "/api/v1/user/loss-authority";
        UserRoleGrantDto userRoleGrantDto = new UserRoleGrantDto();
        ReflectionTestUtils.setField(userRoleGrantDto, "userGrantRole", Role.ROLE_NORMAL.getKey());
        ReflectionTestUtils.setField(userRoleGrantDto, "userId", userId);

        doReturn(userId).when(userService).lossAuthority(any(UserRoleGrantDto.class));
        doReturn(userId).when(userService).approve(any(UserApproveDto.class));
        doReturn(token).when(jwtTokenGenerator).getTokenFromHeader(anyString());
        doReturn(true).when(jwtTokenGenerator).isValidateToken(anyString());
        doReturn("ROLE_ADMIN").when(jwtTokenGenerator).getRoleFromToken(anyString());
        doReturn(true).when(tokenRepository).findToken(anyString());

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .header(AuthConstants.AUTH_HEADER, String.format("%s %s", AuthConstants.TOKEN_TYPE, token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRoleGrantDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        //verify
        verify(userService, times(1)).lossAuthority(any(UserRoleGrantDto.class));
    }

    @DisplayName("????????? ????????? ?????????")
    @Test
    public void userLoginTest() throws Exception {

        //given
        final String url = "/api/v1/user/login";

        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto();
        ReflectionTestUtils.setField(userLoginRequestDto, "userEmail", NORMAL_EMAIL);
        ReflectionTestUtils.setField(userLoginRequestDto, "userPassword", NORMAL_PASSWORD);

        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create(NORMAL_EMAIL, new BCryptPasswordEncoder().encode(NORMAL_PASSWORD));

        CustomUser customUser = new CustomUser(user, Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())));

        doReturn(customUser).when(userService).loadUserByUsername(anyString());
        doReturn(token).when(jwtTokenGenerator).generate(any(CustomUser.class));

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginRequestDto)));

        //then
        MvcResult mvcResult = resultActions.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //verify
        verify(userService, times(1)).loadUserByUsername(anyString());
//        verify(jwtTokenGenerator, times(1)).generate(any(CustomUser.class));

    }


}
