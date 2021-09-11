package com.flextalk.we.user.service;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
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

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

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
