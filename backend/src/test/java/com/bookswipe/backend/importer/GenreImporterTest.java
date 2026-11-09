package com.bookswipe.backend.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreImporterTest {

    private static final int BOOKS_PER_GENRE = 3;
    private static final Genre GENRE = new Genre(1, "Fantasy", "fantasy");
    private static final String LONG_SYNOPSIS =
            "A synopsis that is comfortably longer than fifty characters in total.";
    private static final JsonMapper MAPPER = JsonMapper.builder().build();
    private final ImportSummary summary = new ImportSummary();
    @Mock
    private OpenLibraryClient client;
    @Mock
    private ImportRepository repository;
    private GenreImporter importer;

    private static OpenLibrarySearchResponse.Doc doc(String workId, int popularity) {
        return new OpenLibrarySearchResponse.Doc(
                "/works/" + workId,
                "Title " + workId,
                List.of("Author"),
                List.of("OL1A"),
                2000,
                123,
                300,
                popularity);
    }

    // ---------- happy path ----------

    private static OpenLibraryWork work(String text) {
        return new OpenLibraryWork(MAPPER.readTree("\"" + text + "\""));
    }

    @BeforeEach
    void setUp() {
        var props = new ImporterProperties("test@example.com", 400, 50, 100, BOOKS_PER_GENRE);
        importer = new GenreImporter(props, client, new CandidateFilter(props), repository);

        // sensible defaults: every work has a good synopsis and every save succeeds
        lenient().when(client.fetchWork(anyString())).thenReturn(work(LONG_SYNOPSIS));
        lenient().when(repository.saveBook(any())).thenReturn(true);
    }

    // ---------- stopping conditions ----------

    @Test
    void validBooksAreImportedAndRecordedUnderTheGenre() {
        stubSearch(List.of(doc("OL1W", 500), doc("OL2W", 400)));

        importer.importGenre(GENRE, summary);

        assertThat(summary.imported()).isEqualTo(2);
        assertThat(summary.importedIn("Fantasy")).isEqualTo(2);
        assertThat(summary.skippedTotal()).isZero();
    }

    @Test
    void theSavedBookCarriesTheGenreTheSynopsisAndTheStrippedKey() {
        stubSearch(List.of(doc("OL1W", 500)));

        importer.importGenre(GENRE, summary);

        ArgumentCaptor<ImportedBook> captor = ArgumentCaptor.forClass(ImportedBook.class);
        verify(repository).saveBook(captor.capture());
        ImportedBook saved = captor.getValue();
        assertThat(saved.openLibraryKey()).isEqualTo("OL1W");
        assertThat(saved.genreId()).isEqualTo(GENRE.id());
        assertThat(saved.synopsis()).isEqualTo(LONG_SYNOPSIS);
        assertThat(saved.authors()).hasSize(1);
    }

    @Test
    void stopsAfterBooksPerGenreWithoutAskingForMorePages() {
        stubSearch(
                List.of(
                        doc("OL1W", 500),
                        doc("OL2W", 500),
                        doc("OL3W", 500),
                        doc("OL4W", 500),
                        doc("OL5W", 500)));

        importer.importGenre(GENRE, summary);

        assertThat(summary.imported()).isEqualTo(BOOKS_PER_GENRE);
        verify(client, times(BOOKS_PER_GENRE)).fetchWork(anyString());
        verify(client, times(1)).searchBySubject(eq("fantasy"), anyInt(), anyInt());
    }

    @Test
    void asksForTheNextPageWhenTheFirstOneIsNotEnough() {
        stubSearch(
                List.of(doc("OL1W", 500), doc("OL2W", 500)), // page 1
                List.of(doc("OL3W", 500))); // page 2

        importer.importGenre(GENRE, summary);

        assertThat(summary.imported()).isEqualTo(3);
        verify(client, times(2)).searchBySubject(eq("fantasy"), anyInt(), anyInt());
    }

    // ---------- skipped books ----------

    @Test
    void stopsAtTheFirstBookBelowThePopularityThreshold() {
        stubSearch(List.of(doc("OL1W", 500), doc("OL2W", 99), doc("OL3W", 500)));

        importer.importGenre(GENRE, summary);

        assertThat(summary.imported()).isEqualTo(1);
        assertThat(summary.skipped(SkipReason.LOW_POPULARITY)).isEqualTo(1);
        verify(client, never()).fetchWork("OL3W");
        verify(client, times(1)).searchBySubject(eq("fantasy"), anyInt(), anyInt());
    }

    @Test
    void stopsWhenThePageComesBackEmpty() {
        stubSearch(); // only the empty page

        importer.importGenre(GENRE, summary);

        assertThat(summary.imported()).isZero();
        verify(client, times(1)).searchBySubject(eq("fantasy"), anyInt(), anyInt());
    }

    @Test
    void rejectedCandidatesAreCountedAndTheLoopContinues() {
        var noTitle =
                new OpenLibrarySearchResponse.Doc(
                        "/works/OL1W",
                        null,
                        List.of("Author"),
                        List.of("OL1A"),
                        2000,
                        123,
                        300,
                        500);
        stubSearch(List.of(noTitle, doc("OL2W", 500)));

        importer.importGenre(GENRE, summary);

        assertThat(summary.skipped(SkipReason.NO_TITLE)).isEqualTo(1);
        assertThat(summary.imported()).isEqualTo(1);
    }

    @Test
    void existingBooksAreSkippedWithoutFetchingTheirDescription() {
        stubSearch(List.of(doc("OL1W", 500)));
        when(repository.existsBook("OL1W")).thenReturn(true);

        importer.importGenre(GENRE, summary);

        assertThat(summary.skipped(SkipReason.ALREADY_IMPORTED)).isEqualTo(1);
        verify(client, never()).fetchWork(anyString());
        verify(repository, never()).saveBook(any());
    }

    @Test
    void shortDescriptionsAreSkipped() {
        stubSearch(List.of(doc("OL1W", 500)));
        when(client.fetchWork("OL1W")).thenReturn(work("Too short."));

        importer.importGenre(GENRE, summary);

        assertThat(summary.skipped(SkipReason.SHORT_DESCRIPTION)).isEqualTo(1);
        verify(repository, never()).saveBook(any());
    }

    // ---------- failures must not kill the import ----------

    @Test
    void worksWithoutDescriptionAreSkipped() {
        stubSearch(List.of(doc("OL1W", 500)));
        when(client.fetchWork("OL1W")).thenReturn(new OpenLibraryWork(null));

        importer.importGenre(GENRE, summary);

        assertThat(summary.skipped(SkipReason.NO_DESCRIPTION)).isEqualTo(1);
        verify(repository, never()).saveBook(any());
    }

    @Test
    void anEmptyWorkResponseCountsAsNoDescription() {
        stubSearch(List.of(doc("OL1W", 500)));
        when(client.fetchWork("OL1W")).thenReturn(null);

        importer.importGenre(GENRE, summary);

        assertThat(summary.skipped(SkipReason.NO_DESCRIPTION)).isEqualTo(1);
    }

    @Test
    void aFailedWorkRequestIsCountedAndTheLoopContinues() {
        stubSearch(List.of(doc("OL1W", 500), doc("OL2W", 500)));
        when(client.fetchWork("OL1W")).thenThrow(new RestClientException("boom"));

        importer.importGenre(GENRE, summary);

        assertThat(summary.skipped(SkipReason.FETCH_FAILED)).isEqualTo(1);
        assertThat(summary.imported()).isEqualTo(1);
    }

    @Test
    void aFailedSaveIsCountedAndDoesNotPropagate() {
        stubSearch(List.of(doc("OL1W", 500)));
        when(repository.saveBook(any())).thenThrow(new DataIntegrityViolationException("boom"));

        importer.importGenre(GENRE, summary);

        assertThat(summary.skipped(SkipReason.SAVE_FAILED)).isEqualTo(1);
        assertThat(summary.imported()).isZero();
    }

    // ---------- helpers ----------

    @Test
    void aSaveThatFindsTheBookAlreadyThereCountsAsAlreadyImported() {
        stubSearch(List.of(doc("OL1W", 500)));
        when(repository.saveBook(any())).thenReturn(false);

        importer.importGenre(GENRE, summary);

        assertThat(summary.skipped(SkipReason.ALREADY_IMPORTED)).isEqualTo(1);
        assertThat(summary.imported()).isZero();
    }

    @Test
    void aFailedSearchEndsTheGenreWithoutThrowing() {
        when(client.searchBySubject(anyString(), anyInt(), anyInt()))
                .thenThrow(new RestClientException("down"));

        assertThatCode(() -> importer.importGenre(GENRE, summary)).doesNotThrowAnyException();
        assertThat(summary.imported()).isZero();
    }

    /**
     * Each argument is one page of results; after the last one, every call returns an empty page.
     */
    @SafeVarargs
    private void stubSearch(List<OpenLibrarySearchResponse.Doc>... pages) {
        OngoingStubbing<OpenLibrarySearchResponse> stubbing =
                when(client.searchBySubject(eq("fantasy"), anyInt(), anyInt()));
        for (List<OpenLibrarySearchResponse.Doc> page : pages) {
            stubbing = stubbing.thenReturn(new OpenLibrarySearchResponse(page));
        }
        stubbing.thenReturn(new OpenLibrarySearchResponse(List.of()));
    }
}
