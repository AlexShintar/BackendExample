package ru.shintar.shopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.shintar.shopbackend.dto.CommentDto;
import ru.shintar.shopbackend.dto.Comments;
import ru.shintar.shopbackend.service.CommentService;
import ru.shintar.shopbackend.dto.CreateOrUpdateComment;

import javax.validation.Valid;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@Tag(name = "Комментарии")
@RequiredArgsConstructor
@Validated
public class CommentsController {

    private final CommentService commentService;

    @Operation(summary = "Получение комментариев объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404")
    })
    @GetMapping(value = "/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable int id) {
        log.info("Request to receive all comments from an ad, adId: " + id);
        Comments comments = commentService.getComments(id);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Добавление комментария к объявлению")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404")
    })
    @PostMapping(value = "/{id}/comments")
    public ResponseEntity<CommentDto> addComment(@PathVariable int id,
                                                 @RequestBody @Valid CreateOrUpdateComment text,
                                                 Authentication authentication) {
        log.info("Request to add a comment to ad, adId: " + id);
        CommentDto commentDto = commentService.addComment(id, text, authentication);
        return ResponseEntity.ok(commentDto);
    }

    @Operation(summary = "Удаление комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @PreAuthorize("@customSecurityExpression.hasCommentAuthority(authentication,#commentId )")
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int adId,
                                              @PathVariable int commentId,
                                              Authentication authentication) {
        log.info("Request to remove comment, adId:" + adId);
        commentService.deleteComment(adId, commentId, authentication);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновление комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @PreAuthorize("@customSecurityExpression.hasCommentAuthority(authentication,#commentId )")
    @PatchMapping(value = "/{adId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable int adId,
                                                    @PathVariable int commentId,
                                                    @RequestBody @Valid CreateOrUpdateComment text,
                                                    Authentication authentication) {
        log.info("Request to update comment, adId:" + adId);
        CommentDto commentDto = commentService.updateComment(adId, commentId, text, authentication);
        return ResponseEntity.ok(commentDto);
    }
}