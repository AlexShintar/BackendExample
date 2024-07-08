package ru.shintar.shopbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class Comments {

    private Integer count;
    private List<CommentDto> results;
}
