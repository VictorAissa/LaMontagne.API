package com.victor.lamontagne_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CloudinaryServiceImplTest {
    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    private CloudinaryServiceImpl cloudinaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
        cloudinaryService = new CloudinaryServiceImpl(cloudinary);
    }

    @Test
    void uploadImage_ShouldReturnSecureUrl() throws IOException {
        // Given
        byte[] imageData = "test image".getBytes();
        Map<String, Object> expectedOptions = Map.of(
                "folder", "journeys/images",
                "resource_type", "image"
        );
        String expectedUrl = "https://res.cloudinary.com/test/image/upload/v1/test.jpg";

        // Mock the uploader to return the expected result
        when(uploader.upload(eq(imageData), eq(expectedOptions)))
                .thenReturn(Map.of("secure_url", expectedUrl));

        // When
        String result = cloudinaryService.uploadImage(imageData);

        // Then
        assertEquals(expectedUrl, result);
        verify(uploader).upload(eq(imageData), eq(expectedOptions));
    }

    @Test
    void uploadGpx_ShouldReturnSecureUrl() throws IOException {
        // Given
        byte[] gpxData = "test gpx".getBytes();
        Map<String, Object> expectedOptions = Map.of(
                "folder", "journeys/gpx",
                "resource_type", "raw"
        );
        String expectedUrl = "https://res.cloudinary.com/test/raw/upload/v1/test.gpx";

        when(uploader.upload(eq(gpxData), eq(expectedOptions)))
                .thenReturn(Map.of("secure_url", expectedUrl));

        // When
        String result = cloudinaryService.uploadGpx(gpxData);

        // Then
        assertEquals(expectedUrl, result);
        verify(uploader).upload(eq(gpxData), eq(expectedOptions));
    }

    @Test
    void deleteFile_ShouldCallDestroy() throws IOException {
        // Given
        String publicId = "journeys/images/test";

        // When
        cloudinaryService.deleteFile(publicId);

        // Then
        verify(uploader).destroy(publicId, Map.of());
    }

    @Test
    void extractPublicId_ShouldReturnCorrectId() {
        // Given
        String cloudinaryUrl = "https://res.cloudinary.com/test/image/upload/v1/journeys/images/test.jpg";

        // When
        String result = cloudinaryService.extractPublicId(cloudinaryUrl);

        // Then
        assertEquals("journeys/images/test", result);
    }

    @Test
    void extractPublicId_ShouldHandleUrlWithoutExtension() {
        // Given
        String cloudinaryUrl = "https://res.cloudinary.com/test/image/upload/v1/journeys/images/test";

        // When
        String result = cloudinaryService.extractPublicId(cloudinaryUrl);

        // Then
        assertEquals("journeys/images/test", result);
    }
}