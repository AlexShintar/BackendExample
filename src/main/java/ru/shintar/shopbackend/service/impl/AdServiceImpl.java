package ru.shintar.shopbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.shintar.shopbackend.dto.AdDto;
import ru.shintar.shopbackend.entity.Ad;
import ru.shintar.shopbackend.entity.User;
import ru.shintar.shopbackend.exception.AdNotFoundException;
import ru.shintar.shopbackend.exception.UserNotFoundException;
import ru.shintar.shopbackend.mapper.AdMapper;
import ru.shintar.shopbackend.repository.AdRepository;
import ru.shintar.shopbackend.repository.UserRepository;
import ru.shintar.shopbackend.dto.Ads;
import ru.shintar.shopbackend.dto.CreateOrUpdateAd;
import ru.shintar.shopbackend.dto.ExtendedAd;
import ru.shintar.shopbackend.service.AdService;
import ru.shintar.shopbackend.service.ImageService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final ImageService imageService;

    /**
     * Get all ads
     *
     * @return Ads list
     */
    @Override
    public Ads getAllAds() {
        List<Ad> ads = adRepository.findAll();
        return adMapper.listAdToAds(ads.size(), ads);
    }

    /**
     * Create new ad
     *
     * @param updateAd  new ad
     * @param imageFile image
     * @return AdDTO object
     */
    @Override
    @Transactional
    public AdDto addAd(CreateOrUpdateAd updateAd, MultipartFile imageFile, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        Ad ad = adMapper.adDtoToAd(updateAd);
        ad.setAuthor(user);
        adRepository.saveAndFlush(ad);
        try {
            ad.setImage(imageService.uploadImage("ad" + ad.getId(), imageFile));
            adRepository.saveAndFlush(ad);
            log.info("New ad saved: " + ad);
            return adMapper.adToAdDto(ad);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get full information from ad by id
     *
     * @param id ad id
     * @return object ExtendedAd
     */
    @Override
    public ExtendedAd getAds(Integer id) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(AdNotFoundException::new);
        log.info("Requested information: " + ad);
        return adMapper.adToExtendedAd(ad);
    }

    /**
     * Delete ad by id
     *
     * @param id ad id
     */
    @Override
    @Transactional
    public void removeAd(int id, Authentication authentication) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(AdNotFoundException::new);
        adRepository.deleteById(id);
        log.info("Ad deleted: " + ad);
    }

    /**
     * Update ad by id
     *
     * @param id               ad id
     * @param createOrUpdateAd updated ad
     * @return AdDTO object
     */
    @Override
    @Transactional
    public AdDto updateAds(int id, CreateOrUpdateAd createOrUpdateAd, Authentication authentication) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(AdNotFoundException::new);
        ad.setTitle(createOrUpdateAd.getTitle());
        ad.setPrice(createOrUpdateAd.getPrice());
        ad.setDescription(createOrUpdateAd.getDescription());
        adRepository.save(ad);
        log.info("Ad updated: " + ad);
        return adMapper.adToAdDto(ad);
    }

    /**
     * Get all current user ads
     *
     * @return Ads list
     */
    @Override
    public Ads getAdsMe(Authentication authentication) {
        User author = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        List<Ad> adList = adRepository.findAllByAuthor(author);
        return adMapper.listAdToAds(adList.size(), adList);
    }

    /**
     * Update ad image
     *
     * @param id        ad id
     * @param imageFile ad image
     */
    @Override
    public void updateImage(int id, MultipartFile imageFile, Authentication authentication) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(AdNotFoundException::new);
        try {
            ad.setImage(imageService.uploadImage("ad" + ad.getId(), imageFile));
            adRepository.saveAndFlush(ad);
            log.info("The ad image has been updated");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}