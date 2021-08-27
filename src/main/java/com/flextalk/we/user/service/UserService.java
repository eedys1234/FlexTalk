package com.flextalk.we.user.service;

import com.flextalk.we.cmmn.exception.NotEntityException;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService {

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
}
