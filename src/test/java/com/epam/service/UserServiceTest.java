package com.epam.service;

import com.epam.error.AccessException;
import com.epam.model.User;
import com.epam.model.dto.UserActivateDtoInput;
import com.epam.model.dto.UserDtoInput;
import com.epam.repo.UserRepo;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MeterRegistry meterRegistry;

    private UserDtoInput userDtoInput;

    private User savedUser;

    @BeforeEach
    void setUp() {
        userDtoInput = createTestUserDtoInput();
        savedUser = createTestUser(userDtoInput);
    }

    @Test
    void save_shouldCreateAndReturnUserDtoOutput() {
        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        User result = userService.save(userDtoInput);

        assertNotNull(result);
        assertEquals(userDtoInput.getFirstName(), result.getFirstName());
        assertEquals(userDtoInput.getLastName(), result.getLastName());
        assertEquals(savedUser.getUsername(), result.getUsername());
    }

    @Test
    void createEntireUser_shouldGenerateUniqueUsername() {
        User user = userService.createEntireUser(userDtoInput);

        assertNotNull(user);
        assertTrue(user.getUsername()
                       .startsWith(userDtoInput.getFirstName().toLowerCase() + "." +
                               userDtoInput.getLastName().toLowerCase()));
    }

    @Test
    void createEntireUser_shouldUseBaseUsernameIfUnique() {
        User user = userService.createEntireUser(userDtoInput);

        assertNotNull(user);
        assertEquals(savedUser.getUsername(), user.getUsername());
    }

    @Test
    void isUsernameExistsInDatabase_shouldReturnTrueIfUsernameExists() {
        when(userRepo.existsByUsername(savedUser.getUsername())).thenReturn(true);

        boolean usernameExists = userService.isUsernameExistsInDatabase(savedUser.getUsername());

        assertTrue(usernameExists);
    }

    @Test
    void isUsernameExistsInDatabase_shouldReturnFalseIfUsernameDoesNotExist() {
        when(userRepo.existsByUsername(savedUser.getUsername())).thenReturn(false);

        boolean usernameExists = userService.isUsernameExistsInDatabase(savedUser.getUsername());

        assertFalse(usernameExists);
    }

    @Test
    void switchActivate_WithIncorrectPassword_ShouldThrowAccessException() {
        String username = savedUser.getUsername();
        UserActivateDtoInput userActivateDtoInput = new UserActivateDtoInput(true);

        when(userRepo.findByUsername(savedUser.getUsername())).thenReturn(Optional.of(savedUser));
        when(authenticationService.checkAccess("Incorrect password", savedUser)).thenReturn(true);

        AccessException exception = assertThrows(AccessException.class,
                () -> userService.switchActivate(username, "Incorrect password", userActivateDtoInput));
        assertEquals("You don't have access for this.", exception.getMessage());
    }

    @Test
    void switchActivate_InvalidPassword_ThrowsAccessException() {
        String username = savedUser.getUsername();
        UserActivateDtoInput userActivateDtoInput = new UserActivateDtoInput(true);
        String invalidPassword = "Invalid password";

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(savedUser));
        when(authenticationService.checkAccess(invalidPassword, savedUser)).thenReturn(true);

        AccessException exception = assertThrows(AccessException.class,
                () -> userService.switchActivate(username, invalidPassword, userActivateDtoInput));

        verify(userRepo).findByUsername(username);
        verify(authenticationService).checkAccess(invalidPassword, savedUser);
        verify(userRepo, never()).save(any(User.class));

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    @Test
    void changePassword_ValidOldPassword_SuccessfullyChanged() {
        User updatedUser = createTestUser(userDtoInput);
        updatedUser.setPassword("New password");

        when(userRepo.findByUsername(savedUser.getUsername())).thenReturn(Optional.of(savedUser));
        when(authenticationService.checkAccess(savedUser.getPassword(), savedUser)).thenReturn(false);
        when(userRepo.save(savedUser)).thenReturn(updatedUser);

        userService.changePassword(savedUser.getUsername(), savedUser.getPassword(), updatedUser.getPassword());

        verify(userRepo).findByUsername(savedUser.getUsername());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void changePassword_InvalidOldPassword_ThrowsAccessException() {
        String username = savedUser.getUsername();
        String password = savedUser.getPassword();
        String invalidPassword = "Invalid password";
        User userWithInvalidPassword = createTestUser(userDtoInput);
        userWithInvalidPassword.setPassword(invalidPassword);

        when(userRepo.findByUsername(savedUser.getUsername())).thenReturn(Optional.of(savedUser));
        when(authenticationService.checkAccess(password, savedUser)).thenReturn(true);

        AccessException exception = assertThrows(AccessException.class,
                () -> userService.changePassword(username, password, invalidPassword));

        verify(userRepo).findByUsername(savedUser.getUsername());
        verify(authenticationService).checkAccess(savedUser.getPassword(), savedUser);
        verify(userRepo, never()).save(any(User.class));

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    @Test
    void findUserByUsername_WithValidUserName_ShouldReturnUser() {
        String validUsername = savedUser.getUsername();
        when(userRepo.findByUsernameAndPostfix(validUsername, 0)).thenReturn(Optional.of(savedUser));

        Optional<User> result = userService.findUserByUsername(validUsername);

        assertTrue(result.isPresent());
        assertEquals(savedUser, result.get());
    }

    @Test
    void findUserByUsername_WithUsernameWithPrefix_ShouldReturnUser() {
        String usernameWithPrefix = savedUser.getUsername() + "-1";
        User expectedUser = createTestUser(userDtoInput);
        expectedUser.setUsername(usernameWithPrefix);

        when(userRepo.findByUsernameAndPostfix(savedUser.getUsername(), 1)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.findUserByUsername(usernameWithPrefix);

        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
    }


    private UserDtoInput createTestUserDtoInput() {
        return UserDtoInput.builder().firstName("John").lastName("Doe").build();
    }

    @Test
    void findUserByUsername_WithInvalidUsername_ShouldReturnEmpty() {
        String invalidUsername = "nonexistent.user";

        when(userRepo.findByUsernameAndPostfix(invalidUsername, 0)).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserByUsername(invalidUsername);

        assertTrue(result.isEmpty());
    }

    @Test
    void createEntireUser_WhenUsernameDoesNotExist_ShouldReturnUserWithZeroPrefix() {
        when(userService.isUsernameExistsInDatabase(savedUser.getUsername())).thenReturn(false);

        User resultUser = userService.createEntireUser(userDtoInput);

        assertNotNull(resultUser);
        assertEquals(savedUser.getUsername(), resultUser.getUsername());
        assertEquals(0, resultUser.getPostfix());
    }

    @Test
    void createEntireUser_WhenUsernameExistsWithPrefix_ShouldReturnUserWithIncrementedPrefix() {
        when(userService.isUsernameExistsInDatabase(savedUser.getUsername())).thenReturn(true);
        when(userRepo.findMaxPostfixByUsername(savedUser.getUsername())).thenReturn(2);

        User resultUser = userService.createEntireUser(userDtoInput);

        assertNotNull(resultUser);
        assertEquals(savedUser.getUsername(), resultUser.getUsername());
        assertEquals(3, resultUser.getPostfix());
    }

    @Test
    void login_ShouldOk() {
        String username = "testUser";
        String password = "testPassword";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(authenticationService.checkAccess(password, mockUser)).thenReturn(false);

        userService.login(username, password);

        verify(userRepo).findByUsername(username);
        verify(authenticationService).checkAccess(password, mockUser);
    }

    @Test
    void login_ShouldThrowAccessException() {
        String username = "testUser";
        String password = "testPassword";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(authenticationService.checkAccess(password, mockUser)).thenReturn(true);

        assertThrows(AccessException.class, () -> userService.login(username, password));

        verify(userRepo).findByUsername(username);
        verify(authenticationService).checkAccess(password, mockUser);
    }

    private User createTestUser(UserDtoInput userDtoInput) {
        return User.builder()
                   .id(1L)
                   .firstName(userDtoInput.getFirstName())
                   .lastName(userDtoInput.getLastName())
                   .username(userDtoInput.getFirstName().toLowerCase() + "." + userDtoInput.getLastName().toLowerCase())
                   .password("password")
                   .postfix(0)
                   .build();
    }
}
