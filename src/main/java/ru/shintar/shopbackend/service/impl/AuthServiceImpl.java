package ru.shintar.shopbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.shintar.shopbackend.exception.UserNotFoundException;
import ru.shintar.shopbackend.repository.UserRepository;
import ru.shintar.shopbackend.dto.Register;
import ru.shintar.shopbackend.service.AuthService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Override
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    /**
     * Create new user
     *
     * @param register new user information
     */
    @Override
    public boolean register(Register register) {
        if (manager.userExists(register.getUsername())) {
            return false;
        }
        manager.createUser(
                User.builder()
                        .passwordEncoder(this.encoder::encode)
                        .password(register.getPassword())
                        .username(register.getUsername())
                        .roles(register.getRole().name())
                        .build());

        ru.shintar.shopbackend.entity.User user = userRepository.findUserByEmail(register.getUsername())
                .orElseThrow(UserNotFoundException::new);

        user.setFirstName(register.getFirstName());
        user.setLastName(register.getLastName());
        user.setPhone(register.getPhone());
        user.setRegDate(LocalDateTime.now());
        userRepository.save(user);
        log.info("New user created");
        return true;
    }
}
