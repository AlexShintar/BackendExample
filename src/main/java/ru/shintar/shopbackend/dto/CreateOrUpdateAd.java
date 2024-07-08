package ru.shintar.shopbackend.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CreateOrUpdateAd {

    @NotBlank
    @Size(min = 4, max = 32)
    private String title;

    @Min(0)
    @Max(10000000)
    private Integer price;

    @NotBlank
    @Size(min = 8, max = 64)
    private String description;
}
