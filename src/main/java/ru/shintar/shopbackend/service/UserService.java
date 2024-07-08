package ru.shintar.shopbackend.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.shintar.shopbackend.dto.UserDto;
import ru.shintar.shopbackend.dto.NewPassword;
import ru.shintar.shopbackend.dto.UpdateUser;

public interface UserService {

    boolean setPassword(NewPassword newPassword, Authentication authentication);

    UserDto getUser(Authentication authentication);

    UserDto updateUser(UpdateUser updateUser, Authentication authentication);

    void updateUserImage(MultipartFile image, Authentication authentication);
}
