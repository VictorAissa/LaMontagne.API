package com.victor.lamontagne_api.controller;

import com.victor.lamontagne_api.model.pojo.LoginRequest;
import com.victor.lamontagne_api.model.dto.UserDTO;
import com.victor.lamontagne_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody LoginRequest req) {
        System.out.println("==== Requête reçue sur /api/user/login ====");
        return userService.authenticate(req.email(), req.password());
    }

    @PostMapping("/register")
    @ResponseBody
    public UserDTO register(@RequestBody UserDTO user) {
        return userService.register(user);
    }
}
