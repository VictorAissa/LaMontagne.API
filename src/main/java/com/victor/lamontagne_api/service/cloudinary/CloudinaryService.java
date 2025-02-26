package com.victor.lamontagne_api.service.cloudinary;

public interface CloudinaryService {
    String uploadImage(byte[] image);
    String uploadGpx(byte[] gpx);
    void deleteFile(String publicId);
    String extractPublicId(String cloudinaryUrl);
}
