package ru.shintar.shopbackend.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.shintar.shopbackend.service.AdService;
import ru.shintar.shopbackend.service.CommentService;

@Component
public class CustomSecurityExpression {
    AdService adService;
    CommentService commentService;

    public boolean hasAdAuthority(Authentication authentication, int adId) {
        return authentication.getAuthorities().toString().equals("ROLE_ADMIN") ||
                adService.getAds(adId).getEmail().equals(authentication.getName());
    }

    public boolean hasCommentAuthority(Authentication authentication, int commentId) {
        return authentication.getAuthorities().toString().equals("ROLE_ADMIN") ||
                commentService.getComment(commentId).getAuthor().getEmail().equals(authentication.getName());
    }
}
