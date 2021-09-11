package com.flextalk.we.user.service;

import antlr.StringUtils;
import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.entity.Role;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import com.flextalk.we.user.dto.UserApproveDto;
import com.flextalk.we.user.dto.UserRegisterDto;
import com.flextalk.we.user.dto.UserRoleGrantDto;
import com.flextalk.we.user.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    public Long register(UserRegisterDto userRegisterDto) {
        return userRepository.save(userRegisterDto.toEntity()).getId();
    }

    @Transactional
    public Long update(UserUpdateDto userUpdateDto) {

        User user = findUser(userUpdateDto.getUserId());

        if(Objects.nonNull(userUpdateDto.getUserPassword())) {
            user.updatePassword(userUpdateDto.getUserPassword());
        }

        if(Objects.nonNull(userUpdateDto.getUserProfile())) {
            user.updateProfile(userUpdateDto.getUserProfile());
        }

        return user.getId();
    }

    @Transactional
    public Long approve(UserApproveDto userApproveDto) {

        User user = findUser(userApproveDto.getUserId());
        user.approve();
        return user.getId();
    }

    @Transactional
    public Long grantAuthority(UserRoleGrantDto userRoleGrantDto) {
        User user = findUser(userRoleGrantDto.getUserId());
        user.grantAuthority(Role.valueOf(userRoleGrantDto.getUserGrantRole()));
        return user.getId();
    }

    @Transactional
    public Long loseAuthority(UserRoleGrantDto userRoleGrantDto) {
        User user = findUser(userRoleGrantDto.getUserId());
        user.loseAuthority(Role.valueOf(userRoleGrantDto.getUserGrantRole()));
        return user.getId();
    }

    @Transactional(readOnly = true)
    public User findUser(Long userId) {
        return userRepository.findOne(userId)
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다. userId = " + userId));
    }

    @Transactional(readOnly = true)
    public List<User> findUsers(String[] splitUserIds) {
        return userRepository.findByIds(Arrays.stream(splitUserIds)
                .map(id -> Long.parseLong(id))
                .collect(toList()));
    }

    public boolean findMatchingUsers(List<User> users, String[] splitUserIds) {

        for(String id : splitUserIds)
        {
            if(users.stream().noneMatch(user -> id.equals(String.valueOf(user.getId())))) {
                throw new NotEntityException("사용자가 존재하지 않습니다. userId = " + id);
            }
        }

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(u -> new CustomUser(u, Collections.singleton(new SimpleGrantedAuthority(u.getRole().getKey()))))
                .orElseThrow(() -> new NotEntityException("사용자가 존재하지 않습니다 userEmail = " + username));
    }
}
