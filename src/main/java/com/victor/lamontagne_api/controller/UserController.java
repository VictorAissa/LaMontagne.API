package com.victor.lamontagne_api.controller;

import com.victor.lamontagne_api.model.request.LoginRequest;
import com.victor.lamontagne_api.model.dto.UserDTO;
import com.victor.lamontagne_api.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(
    origins = {"http://localhost:5173", "http://localhost:4200"},
    allowCredentials = "true",
    allowedHeaders = {"Authorization", "Content-Type", "Accept"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
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
