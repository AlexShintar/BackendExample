package ru.shintar.shopbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.shintar.shopbackend.dto.UserDto;
import ru.shintar.shopbackend.entity.Authorities;
import ru.shintar.shopbackend.entity.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "image", expression = "java(user.getImage() != null ? \"/image/\" + user.getImage() : \"\")")
    @Mapping(source = "authorities.authority", target = "role")
    @Mapping(source = "user.id", target = "id")
    UserDto userToUserDto(User user, Authorities authorities);
    }
