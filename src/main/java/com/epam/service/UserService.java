package com.epam.service;

import com.epam.model.User;
import com.epam.model.dto.UserActivateDtoInput;
import com.epam.model.dto.UserDtoInput;

import java.util.Optional;

public interface UserService {

    User save(UserDtoInput userDtoInput);

    void changePassword(String username, String oldPassword, String newPassword);

    void switchActivate(String username, String password, UserActivateDtoInput userInput);

    Optional<User> findUserByUsername(String username);

    void login(String username, String password);
}
