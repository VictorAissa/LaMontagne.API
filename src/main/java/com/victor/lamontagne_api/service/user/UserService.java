package com.victor.lamontagne_api.service.user;

import com.victor.lamontagne_api.model.dto.UserDTO;

public interface UserService {
    String authenticate(String email, String password);
    UserDTO register(UserDTO user);
}
