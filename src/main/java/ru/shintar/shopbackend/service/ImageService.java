package ru.shintar.shopbackend.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ImageService {
    String uploadImage(String imageId, MultipartFile imageFile) throws IOException;
    void getImage(String id, HttpServletResponse response) throws IOException;
}
