package com.flextalk.we.user.cmmn;

import com.flextalk.we.user.domain.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

public class MockLimitUserFactory extends MockUserFactory {

    @Override
    public List<User> createListAddedId() {

        long id = 1L;

        List<User> users = new ArrayList<>();

        for(int i=0;i<100;i++) {
            users.addAll(super.createList());
        }

        for(User user : users)
        {
            ReflectionTestUtils.setField(user, "id", id);
            id+=1;
        }

        return users;
    }
}
