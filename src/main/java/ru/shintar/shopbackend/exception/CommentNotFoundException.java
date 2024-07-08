package ru.shintar.shopbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение CommentNotFoundException
 * Исключение выбрасывается когда в БД не найден комментарий
 * Исключение наследуется от {@link RuntimeException}
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException() {
        super("Comment not found");
    }
}
