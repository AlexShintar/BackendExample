package ru.shintar.shopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.shintar.shopbackend.dto.AdDto;
import ru.shintar.shopbackend.dto.Ads;
import ru.shintar.shopbackend.dto.CreateOrUpdateAd;
import ru.shintar.shopbackend.dto.ExtendedAd;
import ru.shintar.shopbackend.service.AdService;

import javax.validation.Valid;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@Tag(name = "Объявления")
@RequiredArgsConstructor
public class AdsController {

    private final AdService adService;

    @Operation(summary = "Получение всех объявлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AdDto.class))))
    })
    @GetMapping()
    public ResponseEntity<Ads> getAllAds() {
        log.info("Request to receive all ads");
        return ResponseEntity.ok(adService.getAllAds());
    }

    @Operation(summary = "Добавление объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AdDto.class)))),
            @ApiResponse(responseCode = "401")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDto> addAd(@RequestPart("properties") @Valid CreateOrUpdateAd properties,
                                       @RequestPart("image") MultipartFile image,
                                       Authentication authentication) {
        log.info("Request to add an ad");
        return ResponseEntity.ok(adService.addAd(properties, image, authentication));
    }

    @Operation(summary = "Получение информации об объявлении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ExtendedAd.class)))),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable int id) {
        log.info("Request for information about an ad, id:" + id);
        ExtendedAd extendedAd = adService.getAds(id);
        if (extendedAd == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(extendedAd);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @PreAuthorize("@customSecurityExpression.hasAdAuthority(authentication,#id )")
    public ResponseEntity<Void> removeAd(@PathVariable int id,
                                         Authentication authentication) {
        log.info("Request to remove ad, id:" + id);
        adService.removeAd(id, authentication);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление информации об объявлении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AdDto.class)))),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @PreAuthorize("@customSecurityExpression.hasAdAuthority(authentication,#id )")
    public ResponseEntity<AdDto> updateAds(@PathVariable int id,
                                           @RequestBody @Valid CreateOrUpdateAd createOrUpdateAd,
                                           Authentication authentication) {
        log.info("Request to update ad, id:" + id);
        AdDto adDto = adService.updateAds(id, createOrUpdateAd, authentication);
        return ResponseEntity.ok(adDto);
    }

    @Operation(summary = "Получение объявлений авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Ads.class)))),
            @ApiResponse(responseCode = "401")
    })
    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        log.info("Request to receive ads from an authorized user");
        Ads ads = adService.getAdsMe(authentication);
        return ResponseEntity.ok(ads);
    }

    @Operation(summary = "Обновление картинки объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @PreAuthorize("@customSecurityExpression.hasAdAuthority(authentication,#id )")
    @PatchMapping(value = "/{id}/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> updateImage(@PathVariable("id") int id,
                                            @RequestPart("image") MultipartFile image,
                                            Authentication authentication) {
        log.info("Request to update the ad image");
        adService.updateImage(id, image, authentication);
        return ResponseEntity.ok().build();
    }
}