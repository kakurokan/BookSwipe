package com.bookswipe.backend.importer;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("import")
@EnableConfigurationProperties(ImporterProperties.class)
public class ImportRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ImportRunner.class);

    private final ImporterProperties props;
    private final ImportRepository repository;
    private final GenreImporter genreImporter;

    ImportRunner(
            ImporterProperties props, ImportRepository repository, GenreImporter genreImporter) {
        this.props = props;
        this.repository = repository;
        this.genreImporter = genreImporter;
    }

    @Override
    public void run(String @NonNull ... args) {
        List<Genre> genres = repository.findGenres();
        log.info(
                "Importer started: {} genres, up to {} books each",
                genres.size(),
                props.booksPerGenre());

        ImportSummary summary = new ImportSummary();
        for (Genre genre : genres) {
            log.info("Importing genre '{}' ({})", genre.name(), genre.slug());
            genreImporter.importGenre(genre, summary);
        }
        summary.logTo(log);
    }
}
