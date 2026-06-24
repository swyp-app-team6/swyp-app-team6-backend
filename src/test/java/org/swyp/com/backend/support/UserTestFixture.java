package org.swyp.com.backend.support;

import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.user.domain.User;

public class UserTestFixture {

    public static final Long TEST_USER_ID = 1L;
    public static final String TEST_USER_EMAIL = "test@example.com";
    public static final UserRole TEST_ROLE = UserRole.USER;

    public static User createUser(Long id, String email, UserRole role) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "email", email);
        ReflectionTestUtils.setField(user, "role", role);
        return user;
    }
}
