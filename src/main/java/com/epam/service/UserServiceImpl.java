package com.epam.service;

import com.epam.error.AccessException;
import com.epam.error.ErrorMessageConstants;
import com.epam.model.User;
import com.epam.model.dto.UserActivateDtoInput;
import com.epam.model.dto.UserDtoInput;
import com.epam.repo.UserRepo;
import com.epam.util.RandomStringGenerator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    private final AuthenticationService authenticationService;

    private final Counter loginAttemptsCounter;

    @Value("${password.length}")
    private int passwordLength;

    public UserServiceImpl(UserRepo userRepo, AuthenticationService authenticationService,
                           MeterRegistry meterRegistry) {
        this.userRepo = userRepo;
        this.authenticationService = authenticationService;
        this.loginAttemptsCounter = meterRegistry.counter("login_attempts", "outcome", "success");
    }

    @Override
    @Transactional
    public User save(UserDtoInput userDtoInput) {
        log.info("save, userDtoInput = {}", userDtoInput);

        return userRepo.save(createEntireUser(userDtoInput));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("changePassword, username = {}", username);
        User user = getUserByUsername(username);

        if (authenticationService.checkAccess(oldPassword, user)) {
            throw new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE);
        }

        user.setPassword(newPassword);
        userRepo.save(user);
    }

    @Override
    public void switchActivate(String username, String password, UserActivateDtoInput userInput) {
        log.info("switchActivate, username = {}", username);

        User user = getUserByUsername(username);
        if (authenticationService.checkAccess(password, user)) {
            throw new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE);
        }

        user.setIsActive(userInput.getIsActive());
        userRepo.save(user);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        if (Character.isDigit(username.charAt(username.length() - 1))) {
            String[] parts = username.split("-");
            String userNameWithPostfix = parts[0].trim();
            Integer postfix = Integer.valueOf(parts[1]);
            return userRepo.findByUsernameAndPostfix(userNameWithPostfix, postfix);
        }

        return userRepo.findByUsernameAndPostfix(username, 0);
    }

    @Override
    public void login(String username, String password) {
        log.info("changePassword, userName = {}", username);
        User user = getUserByUsername(username);

        if (authenticationService.checkAccess(password, user)) {
            throw new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE);
        }

        if (loginAttemptsCounter != null) {
            loginAttemptsCounter.increment();
        }
    }

    private User getUserByUsername(String username) {
        return userRepo.findByUsername(username)
                       .orElseThrow(() -> new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE));
    }


    public User createEntireUser(UserDtoInput userDtoInput) {
        String password = RandomStringGenerator.generateRandomString(passwordLength);
        String userName = userDtoInput.getFirstName().toLowerCase() + "." + userDtoInput.getLastName().toLowerCase();
        Integer maxPostfix = 0;

        if (isUsernameExistsInDatabase(userName)) {
            maxPostfix = userRepo.findMaxPostfixByUsername(userName);
            maxPostfix++;
        }

        return User.builder()
                   .firstName(userDtoInput.getFirstName())
                   .lastName(userDtoInput.getLastName())
                   .username(userName)
                   .password(password)
                   .postfix(maxPostfix)
                   .isActive(false)
                   .build();

    }

    public boolean isUsernameExistsInDatabase(String username) {
        return userRepo.existsByUsername(username);
    }
}
