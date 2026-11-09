package com.bookswipe.backend.importer;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("import")
@EnableConfigurationProperties(ImporterProperties.class)
public class ImportRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ImportRunner.class);

    private final ImporterProperties props;

    ImportRunner(ImporterProperties props) {
        this.props = props;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        log.info(
                "Importer started (pause={} ms, {} books per genre)",
                props.pauseMs(),
                props.booksPerGenre());
    }
}
