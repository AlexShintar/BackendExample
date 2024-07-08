package ru.shintar.shopbackend.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.shintar.shopbackend.dto.AdDto;
import ru.shintar.shopbackend.dto.Ads;
import ru.shintar.shopbackend.dto.CreateOrUpdateAd;
import ru.shintar.shopbackend.dto.ExtendedAd;

public interface AdService {
    Ads getAllAds();

    AdDto addAd(CreateOrUpdateAd updateAd, MultipartFile image, Authentication authentication);

    ExtendedAd getAds(Integer id);

    void removeAd(int id, Authentication authentication);

    AdDto updateAds(int id, CreateOrUpdateAd createOrUpdateAd, Authentication authentication);

    Ads getAdsMe(Authentication authentication);

    void updateImage(int id, MultipartFile imageFile, Authentication authentication);
}
