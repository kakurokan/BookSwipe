package com.bookswipe.backend.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Component
@Profile("import")
class GenreImporter {

    private static final Logger log = LoggerFactory.getLogger(GenreImporter.class);
    private static final int PAGE_SIZE = 50;
    private static final int MAX_PAGES = 10; // safety net against endless loops

    private final ImporterProperties props;
    private final OpenLibraryClient client;
    private final CandidateFilter filter;
    private final ImportRepository repository;

    GenreImporter(
            ImporterProperties props,
            OpenLibraryClient client,
            CandidateFilter filter,
            ImportRepository repository) {
        this.props = props;
        this.client = client;
        this.filter = filter;
        this.repository = repository;
    }

    void importGenre(Genre genre, ImportSummary summary) {
        summary.startGenre(genre.name());
        int imported = 0;

        for (int page = 1; page <= MAX_PAGES && imported < props.booksPerGenre(); page++) {
            List<OpenLibrarySearchResponse.Doc> docs = fetchPage(genre, page);
            if (docs.isEmpty()) {
                return;
            }
            for (OpenLibrarySearchResponse.Doc doc : docs) {
                if (imported >= props.booksPerGenre()) {
                    return;
                }
                Optional<SkipReason> rejection = filter.reject(doc);
                if (rejection.isPresent()) {
                    summary.recordSkipped(rejection.get());
                    if (rejection.get() == SkipReason.LOW_POPULARITY) {
                        return; // sorted by popularity: everything after is lower
                    }
                    continue;
                }
                if (importBook(doc, genre, summary)) {
                    imported++;
                }
            }
        }
    }

    private List<OpenLibrarySearchResponse.Doc> fetchPage(Genre genre, int page) {
        try {
            OpenLibrarySearchResponse response =
                    client.searchBySubject(genre.slug(), page, PAGE_SIZE);
            return response == null || response.docs() == null ? List.of() : response.docs();
        } catch (RestClientException e) {
            log.warn(
                    "Search failed for genre '{}' page {}: {}", genre.name(), page, e.getMessage());
            return List.of();
        }
    }

    private boolean importBook(
            OpenLibrarySearchResponse.Doc doc, Genre genre, ImportSummary summary) {
        String workId = doc.workId();
        if (repository.existsBook(workId)) {
            summary.recordSkipped(SkipReason.ALREADY_IMPORTED);
            return false;
        }

        OpenLibraryWork work;
        try {
            work = client.fetchWork(workId);
        } catch (RestClientException e) {
            log.warn("Could not fetch work {}: {}", workId, e.getMessage());
            summary.recordSkipped(SkipReason.FETCH_FAILED);
            return false;
        }

        String synopsis =
                SynopsisParser.extract(work == null ? null : work.description()).orElse(null);
        if (synopsis == null) {
            summary.recordSkipped(SkipReason.NO_DESCRIPTION);
            return false;
        }
        if (synopsis.length() < props.minSynopsisLength()) {
            summary.recordSkipped(SkipReason.SHORT_DESCRIPTION);
            return false;
        }

        try {
            boolean saved = repository.saveBook(ImportedBook.from(doc, synopsis, genre.id()));
            if (!saved) {
                summary.recordSkipped(SkipReason.ALREADY_IMPORTED);
                return false;
            }
            summary.recordImported(genre.name());
            return true;
        } catch (DataAccessException e) {
            log.warn("Could not save work {}: {}", workId, e.getMessage());
            summary.recordSkipped(SkipReason.SAVE_FAILED);
            return false;
        }
    }
}
