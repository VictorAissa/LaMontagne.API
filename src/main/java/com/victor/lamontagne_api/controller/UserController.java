package com.victor.lamontagne_api.controller;

import com.victor.lamontagne_api.model.pojo.LoginRequest;
import com.victor.lamontagne_api.model.dto.UserDTO;
import com.victor.lamontagne_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", maxAge = 3600)
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        System.out.println("==== Requête reçue sur /api/user/login ====");
        this.userService = userService;
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody LoginRequest req) {
        return userService.authenticate(req.email(), req.password());
    }

    @PostMapping("/register")
    @ResponseBody
    public UserDTO register(@RequestBody UserDTO user) {
        return userService.register(user);
    }
}
