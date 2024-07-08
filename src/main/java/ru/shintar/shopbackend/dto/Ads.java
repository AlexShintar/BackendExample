package ru.shintar.shopbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class Ads {

    private Integer count;
    private List<AdDto> results;
}
