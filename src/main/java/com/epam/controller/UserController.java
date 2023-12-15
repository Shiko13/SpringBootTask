package com.epam.controller;

import com.epam.model.dto.UserActivateDtoInput;
import com.epam.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api(tags = "User Controller")
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "User login", notes = "Authenticate user with provided credentials and return a response")
    public void login(@RequestParam String username, @RequestParam String password) {
        userService.login(username, password);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Change password",
                  notes = "Change user password based on provided credentials and new password")
    public void changePassword(@RequestParam String username, @RequestParam String oldPassword,
                               @RequestParam String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
    }

    @PatchMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Switch user activity")
    public void switchActivate(@RequestParam String username, @RequestParam String password,
                               @RequestBody UserActivateDtoInput userInput) {
        userService.switchActivate(username, password, userInput);
    }
}
