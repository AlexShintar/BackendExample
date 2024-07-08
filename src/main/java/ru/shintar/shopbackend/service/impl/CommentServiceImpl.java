package ru.shintar.shopbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shintar.shopbackend.dto.CommentDto;
import ru.shintar.shopbackend.entity.Ad;
import ru.shintar.shopbackend.entity.Comment;
import ru.shintar.shopbackend.entity.User;
import ru.shintar.shopbackend.exception.AdNotFoundException;
import ru.shintar.shopbackend.exception.CommentNotFoundException;
import ru.shintar.shopbackend.repository.AdRepository;
import ru.shintar.shopbackend.repository.CommentRepository;
import ru.shintar.shopbackend.repository.UserRepository;
import ru.shintar.shopbackend.dto.Comments;
import ru.shintar.shopbackend.dto.CreateOrUpdateComment;
import ru.shintar.shopbackend.exception.UserNotFoundException;
import ru.shintar.shopbackend.mapper.CommentMapper;
import ru.shintar.shopbackend.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final AdRepository adRepository;

    /**
     * Get all comments by ad id
     *
     * @param id ad id
     * @return Comments list
     */
    @Override
    public Comments getComments(int id) {
        List<Comment> comments = commentRepository.findAllByAdId(id);
        return commentMapper.listCommentToComments(comments.size(), comments);
    }

    /**
     * Getcomments by comment id
     *
     * @param id comment id
     * @return Comment object
     */
    @Override
    public Comment getComment(int id) {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }

    /**
     * Create new comment
     *
     * @param adId ad id
     * @param text new comment
     * @return CommentDto object
     */
    @Override
    @Transactional
    public CommentDto addComment(int adId, CreateOrUpdateComment text, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        Ad ad = adRepository.findById(adId)
                .orElseThrow(AdNotFoundException::new);
        Comment comment = Comment.builder()
                .text(text.getText())
                .author(user)
                .createdAt(LocalDateTime.now())
                .ad(ad)
                .build();
        commentRepository.save(comment);
        log.info("Comment created: " + comment);
        return commentMapper.commentToCommentDto(comment);
    }

    /**
     * Delete comment by id
     *
     * @param adId      ad id
     * @param commentId comment id
     */
    @Override
    @Transactional
    public void deleteComment(int adId, int commentId, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        if (comment.getAd().getId() != adId) {
            throw new AdNotFoundException();
        }
        commentRepository.delete(comment);
        log.info("Comment deleted: " + comment);
    }

    /**
     * Update comment by id
     *
     * @param adId      ad id
     * @param commentId updated id
     * @param text      new comment
     * @return AdDTO object
     */
    @Override
    @Transactional
    public CommentDto updateComment(int adId, int commentId,
                                    CreateOrUpdateComment text, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        if (comment.getAd().getId() != adId) {
            throw new AdNotFoundException();
        }
        comment.setText(text.getText());
        commentRepository.save(comment);
        log.info("Comment updated: " + comment);
        return commentMapper.commentToCommentDto(comment);
    }
}
