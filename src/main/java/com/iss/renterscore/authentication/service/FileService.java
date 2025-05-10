package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {



    public String saveImageFiles(CustomUserDetails userDetails, MultipartFile file, ImageType type) throws IOException {
        if (file == null) return "";
        String homeDir = System.getProperty("user.home");

        String subDir = switch (type) {
            case USER -> "user";
            case HEROES -> "property/heroes";
            case DETAILS -> "property/details";
        };

        Path uploadDir = Paths.get(homeDir, "images", subDir);
        Files.createDirectories(uploadDir);

        String originalFileName = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();
        String fileName = switch (type) {
            case USER -> "user_" + userDetails.getId() + "_" +originalFileName.replace("user_", "");
            case HEROES -> "property_Hero_" + userDetails.getId() + "_" +originalFileName.replace("property_", "");
            case DETAILS -> "property_Details_" + userDetails.getId() + "_" +originalFileName.replace("property_", "");
        };

        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        Path relativePath = Paths.get("images", subDir, fileName);
        return relativePath.toString().replace("\\", "/"); // image path
    }
}


