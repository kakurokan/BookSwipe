package com.bookswipe.backend.importer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Repository
class ImportRepository {

    private static final String LANGUAGE = "eng";

    private static final String INSERT_BOOK =
            """
                    INSERT INTO books (title, synopsis, publication_year, page_count, cover_id,
                                       language, ol_popularity, open_library_key)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT (open_library_key) DO NOTHING
                    RETURNING book_id
                    """;

    private static final String INSERT_AUTHOR =
            """
                    INSERT INTO authors (name, open_library_key) VALUES (?, ?)
                    ON CONFLICT (open_library_key) DO NOTHING
                    """;

    private static final String SELECT_AUTHOR_ID =
            "SELECT author_id FROM authors WHERE open_library_key = ?";

    private static final String INSERT_BOOK_AUTHOR =
            "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

    private static final String INSERT_BOOK_GENRE =
            "INSERT INTO book_genres (book_id, genre_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

    private final JdbcTemplate jdbc;

    ImportRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    List<Genre> findGenres() {
        return jdbc.query(
                "SELECT genre_id, name, slug FROM genres ORDER BY genre_id",
                (rs, rowNum) ->
                        new Genre(
                                rs.getInt("genre_id"), rs.getString("name"), rs.getString("slug")));
    }

    boolean existsBook(String openLibraryKey) {
        Boolean exists =
                jdbc.queryForObject(
                        "SELECT EXISTS (SELECT 1 FROM books WHERE open_library_key = ?)",
                        Boolean.class,
                        openLibraryKey);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Returns false when the book already existed (nothing was written).
     */
    @Transactional
    boolean saveBook(ImportedBook book) {
        List<Long> inserted =
                jdbc.query(
                        INSERT_BOOK,
                        (rs, rowNum) -> rs.getLong(1),
                        book.title(),
                        book.synopsis(),
                        book.publicationYear(),
                        book.pageCount(),
                        book.coverId(),
                        LANGUAGE,
                        book.popularity(),
                        book.openLibraryKey());
        if (inserted.isEmpty()) {
            return false;
        }
        long bookId = inserted.getFirst();

        for (ImportedBook.Author author : book.authors()) {
            long authorId = upsertAuthor(author);
            jdbc.update(INSERT_BOOK_AUTHOR, bookId, authorId);
        }
        jdbc.update(INSERT_BOOK_GENRE, bookId, book.genreId());
        return true;
    }

    private long upsertAuthor(ImportedBook.Author author) {
        jdbc.update(INSERT_AUTHOR, author.name(), author.openLibraryKey());
        return Objects.requireNonNull(
                jdbc.queryForObject(SELECT_AUTHOR_ID, Long.class, author.openLibraryKey()));
    }
}
