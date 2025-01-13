package com.victor.lamontagne_api;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class Application {
    private static final Dotenv dotenv;

    static {
        dotenv = Dotenv.load();
        System.setProperty("MONGO_URL", dotenv.get("MONGO_URL"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static Dotenv getDotenv() {
        return dotenv;
    }
}
