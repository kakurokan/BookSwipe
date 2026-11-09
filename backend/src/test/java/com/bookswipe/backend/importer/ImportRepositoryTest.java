package com.bookswipe.backend.importer;

import com.bookswipe.backend.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        properties = {
                "POSTGRES_DB=test",
                "POSTGRES_USER=test",
                "POSTGRES_PASSWORD=test",
                "JWT_SECRET=um-segredo-de-teste-com-mais-de-32-bytes-1234567890"
        })
@Transactional
class ImportRepositoryTest {

    @Autowired
    private ImportRepository repository;
    @Autowired
    private JdbcTemplate jdbc;

    private int genreId;
    private int otherGenreId;

    @BeforeEach
    void pickGenres() {
        List<Genre> genres = repository.findGenres();
        genreId = genres.get(0).id();
        otherGenreId = genres.get(1).id();
    }

    // ---------- saveBook ----------

    @Test
    void saveBookStoresTheBookItsAuthorAndTheGenreLink() {
        boolean saved = repository.saveBook(book("OL1W", "OL1A", genreId));

        assertThat(saved).isTrue();
        assertThat(count("SELECT count(*) FROM books WHERE open_library_key = ?", "OL1W"))
                .isEqualTo(1);
        assertThat(
                count(
                        """
                                SELECT count(*) FROM book_authors ba
                                JOIN books b ON b.book_id = ba.book_id
                                WHERE b.open_library_key = ?
                                """,
                        "OL1W"))
                .isEqualTo(1);
        assertThat(
                count(
                        """
                                SELECT count(*) FROM book_genres bg
                                JOIN books b ON b.book_id = bg.book_id
                                WHERE b.open_library_key = ? AND bg.genre_id = ?
                                """,
                        "OL1W",
                        genreId))
                .isEqualTo(1);
    }

    @Test
    void savingTheSameWorkTwiceKeepsASingleBookAndIgnoresTheSecondGenre() {
        assertThat(repository.saveBook(book("OL1W", "OL1A", genreId))).isTrue();
        assertThat(repository.saveBook(book("OL1W", "OL1A", otherGenreId))).isFalse();

        assertThat(count("SELECT count(*) FROM books WHERE open_library_key = ?", "OL1W"))
                .isEqualTo(1);
        assertThat(
                count(
                        """
                                SELECT count(*) FROM book_genres bg
                                JOIN books b ON b.book_id = bg.book_id
                                WHERE b.open_library_key = ?
                                """,
                        "OL1W"))
                .isEqualTo(1);
    }

    @Test
    void twoBooksBySameAuthorShareOneAuthorRow() {
        repository.saveBook(book("OL1W", "OL1A", genreId));
        repository.saveBook(book("OL2W", "OL1A", genreId));

        assertThat(count("SELECT count(*) FROM authors WHERE open_library_key = ?", "OL1A"))
                .isEqualTo(1);
    }

    @Test
    void zeroPageCountIsStoredAsNullAndTheWorkKeyLosesItsPrefix() {
        var doc =
                new OpenLibrarySearchResponse.Doc(
                        "/works/OL4W",
                        "Title",
                        List.of("Author"),
                        List.of("OL4A"),
                        2000,
                        123,
                        0,
                        500);

        repository.saveBook(
                ImportedBook.from(doc, "A synopsis long enough for the test.", genreId));

        Integer pages =
                jdbc.queryForObject(
                        "SELECT page_count FROM books WHERE open_library_key = ?",
                        Integer.class,
                        "OL4W");
        assertThat(pages).isNull();
    }

    @Test
    void optionalFieldsMayBeMissing() {
        var book =
                new ImportedBook(
                        "OL9W",
                        "Bare minimum",
                        "A synopsis long enough for the test.",
                        null,
                        null,
                        null,
                        5,
                        List.of(new ImportedBook.Author("OL9A", "Author")),
                        genreId);

        assertThat(repository.saveBook(book)).isTrue();
    }

    // ---------- findBookId / linkGenre / findGenres ----------

    @Test
    void findBookIdReflectsWhatWasSaved() {
        assertThat(repository.findBookId("OL1W")).isEmpty();

        repository.saveBook(book("OL1W", "OL1A", genreId));

        assertThat(repository.findBookId("OL1W")).isPresent();
    }

    @Test
    void linkGenreAddsASecondGenreOnceAndIgnoresRepeats() {
        repository.saveBook(book("OL1W", "OL1A", genreId));
        long bookId = repository.findBookId("OL1W").orElseThrow();

        assertThat(repository.linkGenre(bookId, otherGenreId)).isTrue();
        assertThat(repository.linkGenre(bookId, otherGenreId)).isFalse();
        assertThat(repository.linkGenre(bookId, genreId)).isFalse();

        assertThat(count("SELECT count(*) FROM book_genres WHERE book_id = ?", bookId))
                .isEqualTo(2);
    }

    @Test
    void findGenresReturnsTheSeededGenresOrderedById() {
        List<Genre> genres = repository.findGenres();

        assertThat(genres).hasSize(24);
        assertThat(genres).extracting(Genre::id).isSorted();
        assertThat(genres).extracting(Genre::slug).contains("science_fiction");
    }

    // ---------- helpers ----------

    private ImportedBook book(String workKey, String authorKey, int genre) {
        return new ImportedBook(
                workKey,
                "Title " + workKey,
                "A synopsis long enough for the test.",
                2000,
                300,
                123,
                500,
                List.of(new ImportedBook.Author(authorKey, "Author " + authorKey)),
                genre);
    }

    private int count(String sql, Object... args) {
        return Objects.requireNonNull(jdbc.queryForObject(sql, Integer.class, args));
    }
}
