package com.victor.lamontagne_api.config;

import com.cloudinary.Cloudinary;
import com.victor.lamontagne_api.Application;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    private final String cloudinaryUrl = Application.getDotenv().get("CLOUDINARY_URL");

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }
}
