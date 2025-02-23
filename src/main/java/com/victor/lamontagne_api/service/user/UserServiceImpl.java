package com.victor.lamontagne_api.service.user;

import com.victor.lamontagne_api.exception.UnauthorizedException;
import com.victor.lamontagne_api.model.dto.UserDTO;
import com.victor.lamontagne_api.model.pojo.User;
import com.victor.lamontagne_api.repository.UserRepository;
import com.victor.lamontagne_api.security.JWTService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JWTService jwtService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.jwtService = Objects.requireNonNull(jwtService);
    }

    @Override
    public String authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && BCrypt.checkpw(password, user.get().getPassword())) {
            return jwtService.generateToken(user.get().getId());
        }
        throw new UnauthorizedException("Invalid credentials");
    }

    @Override
    public UserDTO register(UserDTO userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()));

        User savedUser = userRepository.save(user);

        return new UserDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName()
        );
    }
}
