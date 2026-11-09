package com.bookswipe.backend.importer;

import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

final class ImportSummary {

    private final Map<SkipReason, Integer> skipped = new EnumMap<>(SkipReason.class);
    private final Map<String, Integer> importedByGenre = new LinkedHashMap<>();
    private int imported;
    private int linked;

    void startGenre(String genre) {
        importedByGenre.putIfAbsent(genre, 0);
    }

    void recordImported(String genre) {
        imported++;
        importedByGenre.merge(genre, 1, Integer::sum);
    }

    /**
     * An already-imported book that gained one more genre.
     */
    void recordLinked(String genre) {
        linked++;
        importedByGenre.merge(genre, 1, Integer::sum);
    }

    void recordSkipped(SkipReason reason) {
        skipped.merge(reason, 1, Integer::sum);
    }

    int imported() {
        return imported;
    }

    int linked() {
        return linked;
    }

    int importedIn(String genre) {
        return importedByGenre.getOrDefault(genre, 0);
    }

    int skipped(SkipReason reason) {
        return skipped.getOrDefault(reason, 0);
    }

    int skippedTotal() {
        return skipped.values().stream().mapToInt(Integer::intValue).sum();
    }

    void logTo(Logger log) {
        log.info(
                "Import finished: {} new books, {} extra genre links, {} skipped",
                imported,
                linked,
                skippedTotal());
        skipped.forEach((reason, count) -> log.info("  skipped {}: {}", reason, count));
        importedByGenre.forEach((genre, count) -> log.info("  {}: {} added", genre, count));
    }
}
