package com.victor.lamontagne_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "MONGO_URL=mongodb://test:test@localhost:27017/test",
        "JWT_SECRET=test-secret-key-1234567890-test-secret-key",
        "CLOUDINARY_URL=cloudinary://test:test@test"
})
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
