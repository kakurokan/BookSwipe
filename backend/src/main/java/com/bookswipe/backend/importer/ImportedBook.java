package com.bookswipe.backend.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

record ImportedBook(
        String openLibraryKey,
        String title,
        String synopsis,
        Integer publicationYear,
        Integer pageCount,
        Integer coverId,
        int popularity,
        List<Author> authors,
        int genreId) {

    /**
     * Precondition: the doc already passed the CandidateFilter.
     */
    static ImportedBook from(OpenLibrarySearchResponse.Doc doc, String synopsis, int genreId) {
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < doc.authorKeys().size(); i++) {
            authors.add(new Author(doc.authorKeys().get(i), doc.authorNames().get(i)));
        }
        // the schema rejects page_count <= 0, so treat it as "unknown"
        Integer pages = doc.pageCount() != null && doc.pageCount() > 0 ? doc.pageCount() : null;
        return new ImportedBook(
                doc.workId(),
                doc.title(),
                synopsis,
                doc.firstPublishYear(),
                pages,
                doc.coverId(),
                Objects.requireNonNull(doc.wantToReadCount(), "filter should guarantee popularity"),
                authors,
                genreId);
    }

    record Author(String openLibraryKey, String name) {
    }
}
