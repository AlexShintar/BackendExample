package ru.shintar.shopbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.shintar.shopbackend.service.ImageService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Value("${ads.image.dir.path}")
    private String imageDir;

    /**
     * Upload new image
     *
     * @param imageId   image id
     * @param imageFile image
     * @return new image name
     */
    @Override
    public String uploadImage(String imageId, MultipartFile imageFile) throws IOException {
        String imageName = imageId + "." + getExtensions(Objects.requireNonNull(imageFile.getOriginalFilename()));
        Path filePath = Path.of(String.format("%s/%s", imageDir, imageName));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (
                InputStream is = imageFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
        return imageName.replace(".", "_");
    }

    /**
     * Read image from disc
     *
     * @param imageName image name on disc
     */
    @Override
    public void getImage(String imageName, HttpServletResponse response) throws IOException {
        Path filePath = Path.of(String.format("%s/%s", imageDir, imageName.replace("_", ".")));
        try (InputStream is = Files.newInputStream(filePath);
             OutputStream os = response.getOutputStream()) {
            response.setStatus(200);
            is.transferTo(os);
        }
    }

    /**
     * Extract image extension
     *
     * @param fileName full image name
     */
    private String getExtensions(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
