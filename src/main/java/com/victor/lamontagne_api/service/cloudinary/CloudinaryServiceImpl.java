package com.victor.lamontagne_api.service.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;
    private static final String IMAGES_FOLDER = "journeys/images";
    private static final String GPX_FOLDER = "journeys/gpx";

    @Autowired
    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(byte[] image) {
        try {
            Map<String, Object> options = Map.of(
                    "folder", IMAGES_FOLDER,
                    "resource_type", "image"
            );
            Map<?, ?> result = cloudinary.uploader().upload(image, options);
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image to Cloudinary", e);
        }
    }

    @Override
    public String uploadGpx(byte[] gpx) {
        try {
            Map<String, Object> options = Map.of(
                    "folder", GPX_FOLDER,
                    "resource_type", "raw"
            );
            Map<?, ?> result = cloudinary.uploader().upload(gpx, options);
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Error uploading GPX to Cloudinary", e);
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file from Cloudinary", e);
        }
    }

    @Override
    public String extractPublicId(String cloudinaryUrl) {
        try {
            String[] parts = cloudinaryUrl.split("/upload/");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid Cloudinary URL format");
            }

            String fullPath = parts[1].substring(parts[1].indexOf('/') + 1);
            int lastDotIndex = fullPath.lastIndexOf('.');

            return lastDotIndex > 0 ? fullPath.substring(0, lastDotIndex) : fullPath;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Cloudinary URL format: " + cloudinaryUrl);
        }
    }
}
