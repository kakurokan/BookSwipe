package com.bookswipe.backend;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Objects;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        properties = {
                "POSTGRES_DB=test",
                "POSTGRES_USER=test",
                "POSTGRES_PASSWORD=test",
                "JWT_SECRET=um-segredo-de-teste-com-mais-de-32-bytes-1234567890"
        })
@Transactional
public class SchemaConstraintsTest {

    private static final String INSERT_SWIPE =
            "INSERT INTO swipes (user_id, book_id, status, rating) VALUES (?, ?, ?, ?)";
    @Autowired
    private JdbcTemplate jdbc;

    private long userId;
    private long bookId;

    @BeforeEach
    void createBaseData(){
        userId = insertUser("Test User", "test@example.com");
        bookId = insertBook("Test Book", "OL1W");
    }

    private long insertBook(String title, String openLibraryKey) {
        return
    }

    // helpers

    private long insertUser(String name, String email) {
        return Objects.requireNonNull(jdbc.queryForObject(
                "INSERT INTO users(name, email, password_hash) VALUES (?, ?, 'hash')" +" RETURNING user_id",
                Long.class,
                name,
                email));
    }
}
