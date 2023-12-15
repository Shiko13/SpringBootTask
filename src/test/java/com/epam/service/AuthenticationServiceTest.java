package com.epam.service;

import com.epam.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void checkAccess_ValidPassword_ReturnsTrue() {
        String validPassword = "correctPassword";
        User user = new User();
        user.setPassword(validPassword);

        boolean result = !authenticationService.checkAccess(validPassword, user);

        assertTrue(result, "Access should be granted for a valid password");
    }

    @Test
    void checkAccess_InvalidPassword_ReturnsFalse() {
        String validPassword = "correctPassword";
        String invalidPassword = "incorrectPassword";
        User user = new User();
        user.setPassword(invalidPassword);

        boolean result = !authenticationService.checkAccess(validPassword, user);

        assertFalse(result, "Access should be denied for an invalid password");
    }
}
