package com.example.monimentoom.domain.user.controller;

import com.example.monimentoom.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/position")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}
