package com.victor.lamontagne_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureDataMongo
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
