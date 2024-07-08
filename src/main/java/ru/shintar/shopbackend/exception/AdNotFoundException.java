package ru.shintar.shopbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение AdNotFoundException
 * Исключение выбрасывается когда в БД не найдено объявление
 * Исключение наследуется от {@link RuntimeException}
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdNotFoundException extends RuntimeException {

    public AdNotFoundException() {
        super("Ad not found");
    }
}
