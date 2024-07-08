package ru.shintar.shopbackend.service;

import org.springframework.security.core.Authentication;
import ru.shintar.shopbackend.dto.CommentDto;
import ru.shintar.shopbackend.entity.Comment;
import ru.shintar.shopbackend.dto.Comments;
import ru.shintar.shopbackend.dto.CreateOrUpdateComment;

public interface CommentService {
    Comments getComments(int id);

    Comment getComment(int id);

    CommentDto addComment(int adId, CreateOrUpdateComment text, Authentication authentication);

    void deleteComment(int adId, int commentId, Authentication authentication);

    CommentDto updateComment(int adId, int commentId,
                             CreateOrUpdateComment text, Authentication authentication);
}
