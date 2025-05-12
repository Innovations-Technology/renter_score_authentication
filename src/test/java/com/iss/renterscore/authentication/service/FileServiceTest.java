package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.ImageType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

 class FileServiceTest {
    private FileService fileService;

    @TempDir
    Path tempUserHome;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        fileService = new FileService();
        System.setProperty("user.home", tempUserHome.toString());
        userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(42L);
    }

    @AfterEach
    void cleanup() throws IOException {
        FileSystemUtils.deleteRecursively(tempUserHome);
    }

    @Test
    void testSaveImageFiles_userType() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "user_avatar.png", "image/png", "dummy".getBytes());
        String path = fileService.saveImageFiles(userDetails, file, ImageType.USER);

        assertTrue(path.contains("images/user/user_42_avatar.png"));
        assertTrue(Files.exists(tempUserHome.resolve(path)));
    }

    @Test
    void testSaveImageFiles_heroType() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "property_hero.png", "image/png", "dummy".getBytes());
        String path = fileService.saveImageFiles(userDetails, file, ImageType.HEROES);

        assertTrue(path.contains("images/property/heroes/property_Hero_42_hero.png"));
        assertTrue(Files.exists(tempUserHome.resolve(path)));
    }

    @Test
    void testSaveImageFiles_detailsType() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "property_details.png", "image/png", "dummy".getBytes());
        String path = fileService.saveImageFiles(userDetails, file, ImageType.DETAILS);

        assertTrue(path.contains("images/property/details/property_Details_42_details.png"));
        assertTrue(Files.exists(tempUserHome.resolve(path)));
    }

    @Test
    void testSaveImageFiles_nullFile() throws IOException {
        String path = fileService.saveImageFiles(userDetails, null, ImageType.USER);
        assertEquals("", path);
    }
}
