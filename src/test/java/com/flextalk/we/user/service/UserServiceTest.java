package com.flextalk.we.user.service;

import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.cmmn.MockUserInfo;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.entity.Role;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import com.flextalk.we.user.dto.UserApproveDto;
import com.flextalk.we.user.dto.UserRegisterDto;
import com.flextalk.we.user.dto.UserRoleGrantDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static com.flextalk.we.user.cmmn.MockUserInfo.ADMIN_EMAIL;
import static com.flextalk.we.user.cmmn.MockUserInfo.ADMIN_PASSWORD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;


    @DisplayName("사용자 등록 테스트")
    @Test
    public void userRegisterTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User registerUser = mockUserFactory.create(MockUserInfo.NORMAL_EMAIL, MockUserInfo.NORMAL_PASSWORD);
        long userId = 1L;
        ReflectionTestUtils.setField(registerUser, "id", userId);
        String encryptPassword = new BCryptPasswordEncoder().encode(registerUser.getPassword());

        doReturn(registerUser).when(userRepository).save(any(User.class));
        doReturn(encryptPassword).when(bCryptPasswordEncoder).encode(any(String.class));

        UserRegisterDto userRegisterDto = new UserRegisterDto();
        ReflectionTestUtils.setField(userRegisterDto, "userEmail", registerUser.getEmail());
        ReflectionTestUtils.setField(userRegisterDto, "userPassword", registerUser.getPassword());

        //when
        Long addedUserId = userService.register(userRegisterDto);

        //then
        assertThat(addedUserId, is(userId));

        //verify
        verify(userRepository, times(1)).save(any(User.class));
        verify(bCryptPasswordEncoder, times(1)).encode(any(String.class));
    }

    @DisplayName("사용자 정보 수정 테스트")
    @Test
    public void userUpdateTest() {

        //given
        //when
        //then
        //verify
    }

    @DisplayName("사용자 권한 상승 테스트")
    @Test
    public void userGrantAuthorityTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create(MockUserInfo.NORMAL_EMAIL, MockUserInfo.NORMAL_PASSWORD);
        long userId = 1L;
        ReflectionTestUtils.setField(user, "id", userId);

        UserRoleGrantDto userRoleGrantDto = new UserRoleGrantDto();
        ReflectionTestUtils.setField(userRoleGrantDto, "userId", userId);
        ReflectionTestUtils.setField(userRoleGrantDto, "userGrantRole", Role.ROLE_ADMIN.getKey());

        doReturn(Optional.of(user)).when(userRepository).findOne(anyLong());

        //when
        Long grantedUserId = userService.grantAuthority(userRoleGrantDto);

        //then
        assertThat(grantedUserId, is(userId));
        assertThat(user.getRole(), is(Role.ROLE_ADMIN));

        //verify
        verify(userRepository, times(1)).findOne(anyLong());
    }

    @DisplayName("사용자 권한 하락 테스트")
    @Test
    public void userLossAuthorityTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create(MockUserInfo.NORMAL_EMAIL, MockUserInfo.NORMAL_PASSWORD);
        long userId = 1L;
        ReflectionTestUtils.setField(user, "id", userId);
        user.grantAuthority(Role.ROLE_ADMIN);

        assertThat(user.getRole(), is(Role.ROLE_ADMIN));

        UserRoleGrantDto userRoleGrantDto = new UserRoleGrantDto();
        ReflectionTestUtils.setField(userRoleGrantDto, "userId", userId);
        ReflectionTestUtils.setField(userRoleGrantDto, "userGrantRole", Role.ROLE_NORMAL.getKey());

        doReturn(Optional.of(user)).when(userRepository).findOne(anyLong());

        //when
        Long lossUserId = userService.lossAuthority(userRoleGrantDto);

        //then
        assertThat(lossUserId, is(userId));
        assertThat(user.getRole(), is(Role.ROLE_NORMAL));

        //verify
        verify(userRepository, times(1)).findOne(anyLong());
    }

    @DisplayName("권한 GUEST -> NORMAL 테스트")
    @Test
    public void userApproveTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create(MockUserInfo.NORMAL_EMAIL, MockUserInfo.NORMAL_PASSWORD);
        long userId = 1L;
        ReflectionTestUtils.setField(user, "id", userId);
        user.lossAuthority(Role.ROLE_GUEST);
        assertThat(user.getRole(), is(Role.ROLE_GUEST));

        UserApproveDto userApproveDto = new UserApproveDto();
        ReflectionTestUtils.setField(userApproveDto, "userId", userId);

        doReturn(Optional.of(user)).when(userRepository).findOne(anyLong());

        //when
        Long approveUserID = userService.approve(userApproveDto);

        //then
        assertThat(approveUserID, is(userId));
        assertThat(user.getRole(), is(Role.ROLE_NORMAL));

        //verify
        verify(userRepository, times(1)).findOne(anyLong());
    }

    @DisplayName("사용자 로그인 테스트")
    @Test
    public void userLoginTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User adminUser = mockUserFactory.create(ADMIN_EMAIL, ADMIN_PASSWORD);

        doReturn(Optional.of(adminUser)).when(userRepository).findByEmail(anyString());

        //when
        UserDetails userDetails = userService.loadUserByUsername(adminUser.getEmail());

        //then
        assertThat(userDetails.getUsername(), is(ADMIN_EMAIL));
        assertThat(userDetails.getPassword(), is(ADMIN_PASSWORD));

        //verify
        verify(userRepository, times(1)).findByEmail(anyString());
    }
}
