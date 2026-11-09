-- V2: add slug to genres and seed the initial genre list.
-- slug = Open Library subject identifier (used by the importer);
-- name = English text shown to the user.

ALTER TABLE genres
    ADD COLUMN slug VARCHAR(100) NOT NULL UNIQUE;

INSERT INTO genres (name, slug)
VALUES ('Fantasy', 'fantasy'),
       ('Historical Fiction', 'historical_fiction'),
       ('Horror', 'horror'),
       ('Humor', 'humor'),
       ('Mystery & Detective', 'mystery_and_detective_stories'),
       ('Poetry', 'poetry'),
       ('Romance', 'romance'),
       ('Science Fiction', 'science_fiction'),
       ('Short Stories', 'short_stories'),
       ('Thriller', 'thriller'),
       ('Young Adult', 'young_adult_fiction'),
       ('Music', 'music'),
       ('Photography', 'photography'),
       ('Biology', 'biology'),
       ('Mathematics', 'mathematics'),
       ('Physics', 'physics'),
       ('Programming', 'programming'),
       ('Economics', 'economics'),
       ('Ancient Civilizations', 'ancient_civilizations'),
       ('World War II', 'world_war_ii'),
       ('Psychology', 'psychology'),
       ('Religion', 'religion'),
       ('Children''s', 'childrens_literature'),
       ('Biography', 'biography');