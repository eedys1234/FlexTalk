package com.flextalk.we.user.domain;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.Role;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles(value = "test")
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String ADMIN_EMAIL = "test1@gmail.com";
    private final String NORMAL_EMAIL = "test2@gmail.com";

    
    @DisplayName("사용자 등록 테스트")
    @Test
    public void userRegisterTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create(NORMAL_EMAIL, "TES112##513");

        //when
        userRepository.save(user);

        //then
        int userCount = 1;
        List<User> users = userRepository.findAll();
        assertThat(users.size(), equalTo(userCount));
        assertThat(users.get(0).getEmail(), is(user.getEmail()));
    }

    @DisplayName("사용자 수정 테스트")
    @Test
    public void userInfoUpdateTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create(NORMAL_EMAIL, "TES112##513");
        User registerUser = userRepository.save(user);
        String userProfile = "test.png";

        //when
        registerUser.updateProfile(userProfile);
        userRepository.save(registerUser);

        //then
        int userCount = 1;
        List<User> users = userRepository.findAll();
        assertThat(users.size(), equalTo(userCount));
        assertThat(users.get(0).getEmail(), is(user.getEmail()));
    }

    @DisplayName("사용자 승인 테스트")
    @Test
    public void userApproveTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User user = mockUserFactory.create(NORMAL_EMAIL, "TES112##513");
        User registerUser = userRepository.save(user);

        assertThat(registerUser.getRole(), is(Role.ROLE_GUEST));

        //when
        registerUser.approve();

        //then
        assertThat(registerUser.getRole(), is(Role.ROLE_NORMAL));
    }

    @DisplayName("사용자 권한 상승 테스트")
    @Test
    public void userGrantRoleTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User adminUser = mockUserFactory.create(ADMIN_EMAIL, "TES112##513");
        User addedUser = mockUserFactory.create(NORMAL_EMAIL, "TES112##513");

        adminUser.grantAuthority(Role.ROLE_ADMIN);

        userRepository.save(adminUser);
        userRepository.save(addedUser);

        //when
        addedUser.grantAuthority(Role.ROLE_ADMIN);

        //then
        assertThat(addedUser.getRole(), is(Role.ROLE_ADMIN));
    }

    @DisplayName("사용자 권한 상승 실패 테스트")
    @Test
    public void userGrantRoleExceptionTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User adminUser = mockUserFactory.create(ADMIN_EMAIL, "TES112##513");
        User addedUser = mockUserFactory.create(NORMAL_EMAIL, "TES112##513");

        adminUser.grantAuthority(Role.ROLE_ADMIN);
        addedUser.grantAuthority(Role.ROLE_ADMIN);

        userRepository.save(adminUser);
        userRepository.save(addedUser);

        assertThat(addedUser.getRole(), is(Role.ROLE_ADMIN));

        //when
        assertThrows(IllegalArgumentException.class, () -> addedUser.grantAuthority(Role.ROLE_GUEST));
    }

    @DisplayName("사용자 권한 하락 테스트")
    @Test
    public void userLossRoleTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User adminUser = mockUserFactory.create(ADMIN_EMAIL, "TES112##513");
        User addedUser = mockUserFactory.create(NORMAL_EMAIL, "TES112##513");
        adminUser.grantAuthority(Role.ROLE_ADMIN);
        addedUser.grantAuthority(Role.ROLE_ADMIN);
        userRepository.save(adminUser);
        userRepository.save(addedUser);

        assertThat(addedUser.getRole(), is(Role.ROLE_ADMIN));

        //when
        addedUser.lossAuthority(Role.ROLE_NORMAL);

        //then
        assertThat(addedUser.getRole(), is(Role.ROLE_NORMAL));
    }

    @DisplayName("사용자 권한 하락 실패 테스트")
    @Test
    public void userLossRoleExceptionTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User adminUser = mockUserFactory.create(ADMIN_EMAIL, "TES112##513");
        User addedUser = mockUserFactory.create(NORMAL_EMAIL, "TES112##513");

        adminUser.grantAuthority(Role.ROLE_ADMIN);
        addedUser.grantAuthority(Role.ROLE_NORMAL);

        userRepository.save(adminUser);
        userRepository.save(addedUser);

        assertThat(addedUser.getRole(), is(Role.ROLE_NORMAL));

        //when
        assertThrows(IllegalArgumentException.class, () -> addedUser.lossAuthority(Role.ROLE_ADMIN));
    }

    @DisplayName("사용자 로그인 테스트")
    @Test
    public void userLoginTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User adminUser = mockUserFactory.create(ADMIN_EMAIL, "TES112##513");

        userRepository.save(adminUser);

        //when
        User user = userRepository.findByEmail(adminUser.getEmail())
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다."));

        //then
        assertThat(user.getId(), greaterThan(0L));
        assertThat(user.getEmail(), is(ADMIN_EMAIL));
    }

}
