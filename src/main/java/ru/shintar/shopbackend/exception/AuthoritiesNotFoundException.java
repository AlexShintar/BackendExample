package ru.shintar.shopbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение UserNotFoundException
 * Исключение выбрасывается когда в БД не найден пользователь
 * Исключение наследуется от {@link RuntimeException}
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthoritiesNotFoundException extends RuntimeException {

    public AuthoritiesNotFoundException() {
        super("User authority not found");
    }
}