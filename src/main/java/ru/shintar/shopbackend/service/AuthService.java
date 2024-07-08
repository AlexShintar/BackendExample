package ru.shintar.shopbackend.service;

import ru.shintar.shopbackend.dto.Register;

public interface AuthService {
    boolean login(String userName, String password);

    boolean register(Register register);
}
