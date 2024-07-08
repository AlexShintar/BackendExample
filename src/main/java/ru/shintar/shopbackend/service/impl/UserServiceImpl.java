package ru.shintar.shopbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.shintar.shopbackend.dto.UserDto;
import ru.shintar.shopbackend.entity.Authorities;
import ru.shintar.shopbackend.entity.User;
import ru.shintar.shopbackend.exception.AuthoritiesNotFoundException;
import ru.shintar.shopbackend.repository.AuthoritiesRepository;
import ru.shintar.shopbackend.repository.UserRepository;
import ru.shintar.shopbackend.dto.NewPassword;
import ru.shintar.shopbackend.dto.UpdateUser;
import ru.shintar.shopbackend.exception.UserNotFoundException;
import ru.shintar.shopbackend.mapper.UserMapper;
import ru.shintar.shopbackend.service.ImageService;
import ru.shintar.shopbackend.service.UserService;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final AuthoritiesRepository authoritiesRepository;

    /**
     * Set new password
     *
     * @param newPassword new password
     */
    @Transactional
    @Override
    public boolean setPassword(NewPassword newPassword, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            log.warn("Password not updated");
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(user);
        log.info("Password updated");
        return true;
    }

    /**
     * Find user by authentication
     *
     * @param authentication user authenticator
     * @return UserDto object
     */
    @Override
    public UserDto getUser(Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        Authorities authorities = authoritiesRepository.findByUsername(user.getEmail())
                .orElseThrow(AuthoritiesNotFoundException::new);
        UserDto userDto = userMapper.userToUserDto(user, authorities);
        log.info("Requested information: " + userDto);
        return userDto;
    }

    /**
     * Update current user info
     *
     * @param updateUser     user info
     * @param authentication user authenticator
     * @return UserDto object
     */
    @Transactional
    @Override
    public UserDto updateUser(UpdateUser updateUser, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());
        userRepository.save(user);
        Authorities authorities = authoritiesRepository.findByUsername(user.getEmail())
                .orElseThrow(AuthoritiesNotFoundException::new);
        UserDto userDto = userMapper.userToUserDto(user, authorities);
        log.info("User updated: " + userDto);
        return userDto;
    }

    /**
     * Update user image
     *
     * @param imageFile      user image
     * @param authentication user authenticator
     */
    @Override
    public void updateUserImage(MultipartFile imageFile, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        try {
            user.setImage(imageService.uploadImage("user" + user.getId(), imageFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userRepository.saveAndFlush(user);
        log.info("User image has been updated");
    }
}