package com.bookswipe.backend.importer;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("import")
public class CandidateFilter {
    private final ImporterProperties props;

    public CandidateFilter(ImporterProperties props) {
        this.props = props;
    }

    Optional<SkipReason> reject(OpenLibrarySearchResponse.Doc doc) {
        if (doc.key() == null || doc.key().isBlank()) {
            return Optional.of(SkipReason.NO_KEY);
        }

        if (doc.title() == null || doc.title().isBlank()) {
            return Optional.of(SkipReason.NO_TITLE);
        }

        if (!hasMatchingAuthors(doc)) {
            return Optional.of(SkipReason.NO_AUTHOR);
        }

        Integer wanted = doc.wantToReadCount();
        if (wanted == null || wanted < props.minPopularity()) {
            return Optional.of(SkipReason.LOW_POPULARITY);
        }

        return Optional.empty();
    }

    private boolean hasMatchingAuthors(OpenLibrarySearchResponse.Doc doc) {
        return doc.authorNames() != null
                && doc.authorKeys() != null
                && !doc.authorNames().isEmpty()
                && doc.authorNames().size() == doc.authorKeys().size();
    }
}
