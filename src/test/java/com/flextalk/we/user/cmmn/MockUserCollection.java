package com.flextalk.we.user.cmmn;

import com.flextalk.we.user.domain.entity.User;

import java.util.Arrays;
import java.util.List;

public class MockUserCollection {

    List<User> users = Arrays.asList(
            User.register("test_1@gmail.com", "1234!@#$asdf"),
            User.register("test_2@gmail.com", "1234!@#$asdf"),
            User.register("test_3@gmail.com", "1234!@#$asdf"),
            User.register("test_4@gmail.com", "1234!@#$asdf"),
            User.register("test_5@gmail.com", "1234!@#$asdf"),
            User.register("test_6@gmail.com", "1234!@#$asdf"),
            User.register("test_7@gmail.com", "1234!@#$asdf"),
            User.register("test_8@gmail.com", "1234!@#$asdf"),
            User.register("test_9@gmail.com", "1234!@#$asdf"),
            User.register("test_10@gmail.com", "1234!@#$asdf")
    );

    public List<User> create() {
        return users;
    }
}
