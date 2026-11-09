-- V1: initial schema for BookSwipe.
-- Never edit this file after it has been applied; create V2__... instead.

CREATE TABLE users
(
    user_id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Case-insensitive uniqueness (the app also stores emails in lowercase).
CREATE UNIQUE INDEX uq_users_email_lower ON users (lower(email));

CREATE TABLE genres
(
    genre_id    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE authors
(
    author_id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    open_library_key VARCHAR(50)  NOT NULL UNIQUE
);

CREATE TABLE books
(
    book_id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title            VARCHAR(500) NOT NULL,
    synopsis         TEXT,
    publication_year INTEGER,
    page_count       INTEGER,
    cover_id         INTEGER,
    language         VARCHAR(3)   NOT NULL,
    ol_popularity    INTEGER      NOT NULL DEFAULT 0,
    open_library_key VARCHAR(50)  NOT NULL UNIQUE
        CONSTRAINT ck_books_page_count CHECK ( page_count IS NULL OR page_count > 0),
    CONSTRAINT ck_books_ol_popularity CHECK ( ol_popularity >= 0 ),
    CONSTRAINT ck_books_language CHECK (language ~ '^[a-z]{3}$')
);
CREATE INDEX idx_books_popularity ON books (ol_popularity DESC);

CREATE TABLE book_authors
(
    book_id   BIGINT NOT NULL REFERENCES books (book_id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES authors (author_id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);
CREATE INDEX idx_book_authors_author ON book_authors (author_id);

CREATE TABLE book_genres
(
    book_id  BIGINT  NOT NULL REFERENCES books (book_id) ON DELETE CASCADE,
    genre_id INTEGER NOT NULL REFERENCES genres (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);
CREATE INDEX idx_book_genres_genre ON book_genres (genre_id);

CREATE TABLE user_genres
(
    user_id  BIGINT  NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    genre_id INTEGER NOT NULL REFERENCES genres (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, genre_id)
);

CREATE TABLE swipes
(
    swipe_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT      NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    book_id    BIGINT      NOT NULL REFERENCES books (book_id) ON DELETE CASCADE,
    status     VARCHAR(20) NOT NULL,
    rating     NUMERIC(2, 1),
    swiped_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_swipes_user_book UNIQUE (user_id, book_id),
    CONSTRAINT ck_swipes_status CHECK (status IN ('TO_READ', 'REJECTED', 'READ')),
    -- 0.5 to 5.0 in half-point steps, and only when the book was read.
    CONSTRAINT ck_swipes_rating CHECK (
        rating IS NULL
            OR (status = 'READ' AND rating BETWEEN 0.5 AND 5 AND (rating * 2) = trunc(rating * 2))
        )
);

-- Shelf: WHERE user_id = ? AND status = ?
CREATE INDEX idx_swipes_user_status ON swipes (user_id, status);
-- Undo: ORDER BY swiped_at DESC LIMIT 1 for one user
CREATE INDEX idx_swipes_user_swiped_at ON swipes (user_id, swiped_at DESC);
-- Popular: count TO_READ swipes per book
CREATE INDEX idx_swipes_book_status ON swipes (book_id, status);