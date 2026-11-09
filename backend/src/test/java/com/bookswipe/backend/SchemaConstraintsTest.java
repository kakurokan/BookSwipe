package com.bookswipe.backend;

import jakarta.transaction.Transactional;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

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
    @Autowired
    private EmailValidator emailValidator;

    @BeforeEach
    void createBaseData() {
        userId = insertUser("Test User", "test@example.com");
        bookId = insertBook("Test Book", "OL1W");
    }

    // seed

    @Test
    void seedHas24GenresWithUniqueSlugs() {
        Integer genres = jdbc.queryForObject("SELECT count(*) FROM genres", Integer.class);
        Integer slugs = jdbc.queryForObject("SELECT count(DISTINCT slug) FROM genres", Integer.class);

        assertThat(genres).isEqualTo(24);
        assertThat(slugs).isEqualTo(24);
    }

    // swipes.rating

    @ParameterizedTest
    @CsvSource({"READ,0.5", "READ,3.5", "READ,5"})
    void ratingAcceptsHalfPointsWhenRead(String status, BigDecimal rating) {
        assertThatCode(() -> jdbc.update(INSERT_SWIPE, userId, bookId, status, rating))
                .doesNotThrowAnyException();
    }

    @Test
    void ratingMayBeNull() {
        assertThatCode(() -> jdbc.update(INSERT_SWIPE, userId, bookId, "READ", null))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({
            "READ,3.7", // not a half point
            "READ,0", // below the minimum
            "READ,5.5", // above the maximum
            "TO_READ,4", // rating only allowed with READ
            "REJECTED,4"
    })
    void ratingIsRejectedWhenInvalid(String status, BigDecimal rating) {
        assertThatCode(() -> jdbc.update(INSERT_SWIPE, userId, bookId, status, rating))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("ck_swipes_status");
    }

    // helpers

    private long insertBook(String title, String openLibraryKey) {
        return Objects.requireNonNull(jdbc.queryForObject(
                "INSERT INTO books (title, language, open_library_key) VALUES (?, 'eng', ?)" +
                        " RETURNING  book_id",
                Long.class,
                title,
                openLibraryKey
        ));
    }

    private long insertUser(String name, String email) {
        return Objects.requireNonNull(jdbc.queryForObject(
                "INSERT INTO users(name, email, password_hash) VALUES (?, ?, 'hash')" + " RETURNING user_id",
                Long.class,
                name,
                email));
    }
}
