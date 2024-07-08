package ru.shintar.shopbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.shintar.shopbackend.dto.AdDto;
import ru.shintar.shopbackend.entity.Ad;
import ru.shintar.shopbackend.dto.Ads;
import ru.shintar.shopbackend.dto.CreateOrUpdateAd;
import ru.shintar.shopbackend.dto.ExtendedAd;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AdMapper {
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(target = "image", expression = "java(ad.getImage() != null ? \"/image/\" + ad.getImage() : \"\")")
    AdDto adToAdDto(Ad ad);

    Ads listAdToAds(int count, List<Ad> results);

    Ad adDtoToAd(CreateOrUpdateAd createOrUpdateAd);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "author.email", target = "email")
    @Mapping(source = "author.phone", target = "phone")
    @Mapping(target = "image", expression = "java(ad.getImage() != null ? \"/image/\" + ad.getImage() : \"\")")
    ExtendedAd adToExtendedAd(Ad ad);
}