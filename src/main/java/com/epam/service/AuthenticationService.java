package com.epam.service;

import com.epam.model.User;

public interface AuthenticationService {

    boolean checkAccess(String password, User user);
}
