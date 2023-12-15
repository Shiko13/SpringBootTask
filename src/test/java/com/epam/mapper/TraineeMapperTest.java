package com.epam.mapper;

import com.epam.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraineeMapperTest {

    private final TraineeMapper traineeMapper = new TraineeMapperImpl();

    @Test
    void appendPostfixWithNonZeroPostfix_ShouldOk() {
        User user = new User();
        user.setUsername("testUser");
        user.setPostfix(1);

        String result = traineeMapper.appendPostfix(user);

        assertEquals("testUser-1", result);
    }

    @Test
    void appendPostfixWithZeroPostfix_ShouldOk() {
        User user = new User();
        user.setUsername("testUser");
        user.setPostfix(0);

        String result = traineeMapper.appendPostfix(user);

        assertEquals("testUser", result);
    }
}
