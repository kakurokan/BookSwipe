package com.bookswipe.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {
        "POSTGRES_DB=test",
        "POSTGRES_USER=test",
        "POSTGRES_PASSWORD=test",
        "JWT_SECRET=um-segredo-de-teste-com-mais-de-32-bytes-1234567890"
})
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
