package com.bookswipe.backend.importer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CandidateFilterTest {

    private static final int MIN_POPULARITY = 100;

    private final CandidateFilter filter =
            new CandidateFilter(
                    new ImporterProperties("test@example.com", 400, 50, MIN_POPULARITY, 20));

    private static OpenLibrarySearchResponse.Doc validDoc() {
        return doc("Title", List.of("Author"), List.of("OL1A"), 500);
    }

    private static OpenLibrarySearchResponse.Doc doc(
            String title, List<String> authorNames, List<String> authorKeys, Integer wanted) {
        return new OpenLibrarySearchResponse.Doc(
                "/works/OL1W", title, authorNames, authorKeys, 2000, 123, 300, wanted);
    }

    @Test
    void validBookIsNotRejected() {
        assertThat(filter.reject(validDoc())).isEmpty();
    }

    @Test
    void missingKeyIsRejected() {
        var doc =
                new OpenLibrarySearchResponse.Doc(
                        null, "Title", List.of("Author"), List.of("OL1A"), 2000, 123, 300, 500);

        assertThat(filter.reject(doc)).contains(SkipReason.NO_KEY);
    }

    @Test
    void blankKeyIsRejected() {
        var doc =
                new OpenLibrarySearchResponse.Doc(
                        "  ", "Title", List.of("Author"), List.of("OL1A"), 2000, 123, 300, 500);

        assertThat(filter.reject(doc)).contains(SkipReason.NO_KEY);
    }

    @Test
    void blankTitleIsRejected() {
        var doc = doc("   ", List.of("Author"), List.of("OL1A"), 500);

        assertThat(filter.reject(doc)).contains(SkipReason.NO_TITLE);
    }

    @Test
    void missingTitleIsRejected() {
        var doc = doc(null, List.of("Author"), List.of("OL1A"), 500);

        assertThat(filter.reject(doc)).contains(SkipReason.NO_TITLE);
    }

    @Test
    void emptyAuthorListIsRejected() {
        var doc = doc("Title", List.of(), List.of(), 500);

        assertThat(filter.reject(doc)).contains(SkipReason.NO_AUTHOR);
    }

    @Test
    void missingAuthorFieldsAreRejected() {
        var doc = doc("Title", null, null, 500);

        assertThat(filter.reject(doc)).contains(SkipReason.NO_AUTHOR);
    }

    @Test
    void authorNamesAndKeysWithDifferentSizesAreRejected() {
        var doc = doc("Title", List.of("Author One", "Author Two"), List.of("OL1A"), 500);

        assertThat(filter.reject(doc)).contains(SkipReason.NO_AUTHOR);
    }

    @Test
    void missingPopularityIsRejected() {
        var doc = doc("Title", List.of("Author"), List.of("OL1A"), null);

        assertThat(filter.reject(doc)).contains(SkipReason.LOW_POPULARITY);
    }

    // helpers

    @Test
    void popularityBelowTheThresholdIsRejected() {
        var doc = doc("Title", List.of("Author"), List.of("OL1A"), MIN_POPULARITY - 1);

        assertThat(filter.reject(doc)).contains(SkipReason.LOW_POPULARITY);
    }

    @Test
    void popularityExactlyAtTheThresholdIsAccepted() {
        var doc = doc("Title", List.of("Author"), List.of("OL1A"), MIN_POPULARITY);

        assertThat(filter.reject(doc)).isEmpty();
    }
}
