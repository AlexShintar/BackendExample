package ru.shintar.shopbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.shintar.shopbackend.dto.CommentDto;
import ru.shintar.shopbackend.entity.Comment;
import ru.shintar.shopbackend.dto.Comments;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTimeToLong")
    @Mapping(target = "authorImage", expression =
            "java(comment.getAuthor().getImage() != null ? \"/image/\" + comment.getAuthor().getImage() : \"\")")
    CommentDto commentToCommentDto(Comment comment);

    Comments listCommentToComments(int count, List<Comment> results);

    @Named("localDateTimeToLong")
    default Long localDateTimeToLong(LocalDateTime dateTime) {
        return dateTime.toInstant(ZonedDateTime.now().getOffset()).toEpochMilli();
    }
}
