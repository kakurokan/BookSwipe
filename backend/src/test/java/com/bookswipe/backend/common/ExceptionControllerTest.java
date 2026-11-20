package com.bookswipe.backend.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookswipe.backend.common.Exception.BookNotFoundException;
import com.bookswipe.backend.common.Exception.EmailAlreadyUsedException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

class ExceptionControllerTest {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc =
                MockMvcBuilders.standaloneSetup(new FakeController())
                        .setControllerAdvice(new ExceptionController())
                        .build();
    }

    @Test
    void invalidBodyReturns400WithTheFieldThatFailed() throws Exception {
        mvc.perform(
                        post("/fake/validation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"Marta\",\"email\":\"nao-e-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.details[0].field").value("email"));
    }

    @Test
    void malformedJsonReturns400WithoutDetails() throws Exception {
        mvc.perform(
                        post("/fake/validation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").doesNotExist());
    }

    @Test
    void wrongParameterTypeReturns400() throws Exception {
        mvc.perform(get("/fake/type").param("color", "XPTO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0].field").value("color"));
    }

    @Test
    void domainNotFoundReturns404WithItsOwnCode() throws Exception {
        mvc.perform(get("/fake/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void conflictReturns409WithTheExceptionCode() throws Exception {
        mvc.perform(get("/fake/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_USED"));
    }

    @Test
    void unexpectedErrorReturns500WithoutLeakingTheCause() throws Exception {
        mvc.perform(get("/fake/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @RestController
    @RequestMapping("/fake")
    static class FakeController {
        @PostMapping("/validation")
        void validation(@Valid @RequestBody Body body) {}

        @GetMapping("/type")
        void type(@RequestParam Color color) {}

        @GetMapping("/not-found")
        void notFound() {
            throw new BookNotFoundException(7);
        }

        @GetMapping("/conflict")
        void conflict() {
            throw new EmailAlreadyUsedException();
        }

        @GetMapping("/boom")
        void boom() {
            throw new IllegalStateException("Maior segredo da história");
        }

        enum Color {
            RED,
            BLUE
        }

        record Body(@NotBlank String name, @Email String email) {}
    }
}
