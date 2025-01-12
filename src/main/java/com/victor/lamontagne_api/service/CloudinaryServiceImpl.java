package com.victor.lamontagne_api.service;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(byte[] image) {
        return "";
    }

    @Override
    public String uploadGpx(byte[] gpx) {
        return "";
    }

    @Override
    public void deleteFile(String publicId) {

    }
}
