package com.flextalk.we.user.cmmn;

import com.flextalk.we.user.domain.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MockUserFactory {

    String[][] userInfo = {
            {"test_1@gmail.com", "1234!@#$asdf"},
            {"test_2@gmail.com", "1234!@#$asdf"},
            {"test_3@gmail.com", "1234!@#$asdf"},
            {"test_4@gmail.com", "1234!@#$asdf"},
            {"test_5@gmail.com", "1234!@#$asdf"},
            {"test_6@gmail.com", "1234!@#$asdf"},
            {"test_7@gmail.com", "1234!@#$asdf"},
            {"test_8@gmail.com", "1234!@#$asdf"},
            {"test_9@gmail.com", "1234!@#$asdf"},
            {"test_1@gmail.com", "1234!@#$asdf"},
    };

    public User create() {
        return User.register(userInfo[0][0], userInfo[0][1]);
    }

    public User createAddedId(Long id) {
        User user = create();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public List<User> createList() {
        return Arrays.stream(userInfo)
                .map(info -> User.register(info[0], info[1]))
                .collect(toList());
    }

    public List<User> createListAddedId() {
        List<User> users = createList();

        long id = 1L;

        for(User user : users)
        {
            ReflectionTestUtils.setField(user, "id", id);
            id+=1;
        }

        return users;
    }
}
