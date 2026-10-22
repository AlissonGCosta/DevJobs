package br.costa.DevJobs.infrastructure.controller;

import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcPrint;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:devjobs_users_api_test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.main.banner-mode=off",
        "logging.level.root=ERROR"
})
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class UsersApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UsersRepository repository;
    @Autowired PasswordEncoder passwordEncoder;

    private static final Instant CREATED = Instant.parse("2026-01-01T00:00:00Z");
    private static final String PASSWORD = "password123";

    @BeforeEach
    void clearOnlyTheIsolatedTestDatabase() {
        repository.deleteAll();
    }

    @Test
    void registrationReturnsCreatedAndStoresOnlyPasswordHash() throws Exception {
        var response = mvc.perform(post("/v1/users").contentType(APPLICATION_JSON)
                        .content(registration("Pessoa Exemplo", "new@example.test", PASSWORD, PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.fullName").value("Pessoa Exemplo"))
                .andExpect(jsonPath("$.email").value("new@example.test"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.confirmPassword").doesNotExist())
                .andReturn().getResponse();

        Number id = JsonPath.read(response.getContentAsString(), "$.id");
        UsersEntity saved = repository.findById(id.longValue()).orElseThrow();
        assertAll(
                () -> assertEquals("Pessoa Exemplo", saved.getFullName()),
                () -> assertEquals("new@example.test", saved.getEmail()),
                () -> assertNotEquals(PASSWORD, saved.getPassword()),
                () -> assertTrue(passwordEncoder.matches(PASSWORD, saved.getPassword())),
                () -> assertEquals(UserRoleEnum.ROLE_USER, saved.getRole()),
                () -> assertEquals(0, saved.getAttempts()),
                () -> assertNotNull(saved.getCreatedAt()),
                () -> assertNotNull(saved.getUpdatedAt())
        );
    }

    @Test
    void duplicateRegistrationReturnsConflictWithoutCreatingAnotherUser() throws Exception {
        fixture("Pessoa Original", "occupied@example.test");
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON)
                        .content(registration("Outra Pessoa", "occupied@example.test", PASSWORD, PASSWORD)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/v1/users"));
        assertEquals(1, repository.count());
    }

    @ParameterizedTest
    @ValueSource(strings = {"fullName", "email", "password", "confirmPassword"})
    void registrationRejectsMissingRequiredFields(String field) throws Exception {
        String body = JsonPath.parse(registration("Pessoa Exemplo", "new@example.test", PASSWORD, PASSWORD))
                .delete("$." + field).jsonString();
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @ParameterizedTest
    @ValueSource(strings = {"not-an-email", "", "   "})
    void registrationRejectsMalformedOrBlankEmail(String email) throws Exception {
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON)
                        .content(registration("Pessoa Exemplo", email, PASSWORD, PASSWORD)))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void registrationRejectsWhitespaceOnlyName() throws Exception {
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON)
                        .content(registration("      ", "new@example.test", PASSWORD, PASSWORD)))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void registrationRejectsInvalidDomainName() throws Exception {
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON)
                        .content(registration("Ana", "new@example.test", PASSWORD, PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        assertEquals(0, repository.count());
    }

    @Test
    void registrationRejectsShortPassword() throws Exception {
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON)
                        .content(registration("Pessoa Exemplo", "new@example.test", "short", "short")))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void registrationRejectsDifferentConfirmation() throws Exception {
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON)
                        .content(registration("Pessoa Exemplo", "new@example.test", PASSWORD, "different123")))
                .andExpect(status().isConflict());
        assertEquals(0, repository.count());
    }

    @Test
    void malformedJsonIsRejected() throws Exception {
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON).content("{not-json"))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void missingRequestBodyIsRejected() throws Exception {
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inputValidationErrorsHaveTheSameEnvelopeAsDomainErrors() throws Exception {
        mvc.perform(post("/v1/users").contentType(APPLICATION_JSON)
                        .content(registration("Pessoa Exemplo", "new@example.test", PASSWORD, PASSWORD)
                                .replace("\"email\":\"new@example.test\"", "\"email\":null")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/v1/users"))
                .andExpect(jsonPath("$.errors", not(empty())))
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    void findByIdReturnsPublicUserFields() throws Exception {
        UsersEntity user = fixture("Pessoa Exemplo", "person@example.test");
        mvc.perform(get("/v1/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().intValue()))
                .andExpect(jsonPath("$.fullName").value("Pessoa Exemplo"))
                .andExpect(jsonPath("$.email").value("person@example.test"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.confirmPassword").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PUT", "PATCH", "DELETE"})
    void operationsOnMissingUserReturnNotFound(String method) throws Exception {
        var request = request(HttpMethod.valueOf(method), "/v1/users/999999999")
                .contentType(APPLICATION_JSON);
        if (method.equals("PUT")) {
            request.content(profile("Pessoa Atualizada", "updated@example.test"));
        } else if (method.equals("PATCH")) {
            request.content(passwordChange(PASSWORD, "newPassword123", "newPassword123"));
        }
        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/v1/users/999999999"));
    }

    @Test
    void nonNumericIdReturnsBadRequest() throws Exception {
        mvc.perform(get("/v1/users/not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listWithoutUsersReturnsEmptyJsonArray() throws Exception {
        mvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void listReturnsAllUsersWithoutCredentialFields() throws Exception {
        fixture("Primeira Pessoa", "first@example.test");
        fixture("Segunda Pessoa", "second@example.test");
        mvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("first@example.test", "second@example.test")))
                .andExpect(jsonPath("$[*].fullName", containsInAnyOrder("Primeira Pessoa", "Segunda Pessoa")))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].password").doesNotExist());
    }

    @Test
    void profileUpdateAllowsKeepingOwnEmailAndPersistsCorrectFields() throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        String hash = user.getPassword();

        mvc.perform(put("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(profile("Pessoa Atualizada", "original@example.test")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().intValue()))
                .andExpect(jsonPath("$.fullName").value("Pessoa Atualizada"))
                .andExpect(jsonPath("$.email").value("original@example.test"));

        UsersEntity saved = repository.findById(user.getId()).orElseThrow();
        assertAll(
                () -> assertEquals("Pessoa Atualizada", saved.getFullName()),
                () -> assertEquals("original@example.test", saved.getEmail()),
                () -> assertEquals(hash, saved.getPassword()),
                () -> assertEquals(CREATED, saved.getCreatedAt())
        );
    }

    @Test
    void profileUpdateAcceptsAvailableEmailWithoutSwappingFields() throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        mvc.perform(put("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(profile("Pessoa Atualizada", "available@example.test")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Pessoa Atualizada"))
                .andExpect(jsonPath("$.email").value("available@example.test"));
        UsersEntity saved = repository.findById(user.getId()).orElseThrow();
        assertEquals("available@example.test", saved.getEmail());
        assertEquals("Pessoa Atualizada", saved.getFullName());
    }

    @Test
    void profileUpdateReturnsModificationDateInsteadOfCreationDate() throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        var response = mvc.perform(put("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(profile("Pessoa Atualizada", "original@example.test")))
                .andExpect(status().isOk()).andReturn().getResponse();
        String value = JsonPath.read(response.getContentAsString(), "$.updatedAt");
        assertTrue(Instant.parse(value).isAfter(CREATED),
                "updatedAt must represent this modification, not the original creation date");
    }

    @Test
    void profileUpdateRejectsAnotherUsersEmailAndPreservesStoredData() throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        fixture("Outra Pessoa", "occupied@example.test");
        mvc.perform(put("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(profile("Pessoa Atualizada", "occupied@example.test")))
                .andExpect(status().isConflict());
        UsersEntity saved = repository.findById(user.getId()).orElseThrow();
        assertEquals("original@example.test", saved.getEmail());
        assertEquals("Pessoa Original", saved.getFullName());
    }

    @Test
    void profileUpdateRejectsInvalidNameBeforeChangingStoredData() throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        mvc.perform(put("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(profile("Ana", "original@example.test")))
                .andExpect(status().isBadRequest());
        assertEquals("Pessoa Original", repository.findById(user.getId()).orElseThrow().getFullName());
    }

    @Test
    void passwordChangeStoresNewHashUpdatesDateAndPreservesOtherFields() throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        String previousHash = user.getPassword();
        mvc.perform(patch("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(passwordChange(PASSWORD, "newPassword123", "newPassword123")))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        UsersEntity saved = repository.findById(user.getId()).orElseThrow();
        assertAll(
                () -> assertNotEquals(previousHash, saved.getPassword()),
                () -> assertTrue(passwordEncoder.matches("newPassword123", saved.getPassword())),
                () -> assertFalse(passwordEncoder.matches(PASSWORD, saved.getPassword())),
                () -> assertTrue(saved.getUpdatedAt().isAfter(CREATED)),
                () -> assertEquals(CREATED, saved.getCreatedAt()),
                () -> assertEquals("original@example.test", saved.getEmail()),
                () -> assertEquals(UserRoleEnum.ROLE_USER, saved.getRole())
        );
    }

    @Test
    void wrongOldPasswordDoesNotChangeStoredHashOrDate() throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        mvc.perform(patch("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(passwordChange("incorrect123", "newPassword123", "newPassword123")))
                .andExpect(status().isConflict());
        UsersEntity saved = repository.findById(user.getId()).orElseThrow();
        assertEquals(user.getPassword(), saved.getPassword());
        assertEquals(CREATED, saved.getUpdatedAt());
    }

    @Test
    void differentPasswordConfirmationDoesNotChangeStoredHash() throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        mvc.perform(patch("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(passwordChange(PASSWORD, "newPassword123", "different123")))
                .andExpect(status().isConflict());
        assertEquals(user.getPassword(), repository.findById(user.getId()).orElseThrow().getPassword());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 7, 65})
    void passwordChangeRejectsLengthOutsideDomainPolicyWithoutSaving(int length) throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        String password = "a".repeat(length);
        mvc.perform(patch("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(passwordChange(PASSWORD, password, password)))
                .andExpect(status().isBadRequest());
        assertEquals(user.getPassword(), repository.findById(user.getId()).orElseThrow().getPassword());
    }

    @Test
    void deleteReturnsNoContentAndUserCannotBeFoundAfterward() throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        mvc.perform(delete("/v1/users/{id}", user.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertFalse(repository.existsById(user.getId()));
        mvc.perform(get("/v1/users/{id}", user.getId())).andExpect(status().isNotFound());
    }


    @ParameterizedTest
    @ValueSource(strings = {"fullName", "email"})
    void profileUpdateRejectsMissingRequiredFields(String field) throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        String body = JsonPath.parse(profile("Pessoa Atualizada", "original@example.test"))
                .delete("$." + field).jsonString();
        mvc.perform(put("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertEquals("Pessoa Original", repository.findById(user.getId()).orElseThrow().getFullName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"not-an-email", "", "   "})
    void profileUpdateRejectsMalformedOrBlankEmail(String email) throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        mvc.perform(put("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON)
                        .content(profile("Pessoa Atualizada", email)))
                .andExpect(status().isBadRequest());
        assertEquals("original@example.test", repository.findById(user.getId()).orElseThrow().getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = {"oldPassword", "newPassword", "confirmNewPassword"})
    void passwordChangeRejectsMissingRequiredFields(String field) throws Exception {
        UsersEntity user = fixture("Pessoa Original", "original@example.test");
        String body = JsonPath.parse(passwordChange(PASSWORD, "newPassword123", "newPassword123"))
                .delete("$." + field).jsonString();
        mvc.perform(patch("/v1/users/{id}", user.getId()).contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertEquals(user.getPassword(), repository.findById(user.getId()).orElseThrow().getPassword());
    }

    @Test
    void unsupportedMediaTypeReturns415() throws Exception {
        mvc.perform(post("/v1/users").contentType("text/plain").content("not JSON"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void unsupportedHttpMethodReturns405() throws Exception {
        mvc.perform(post("/v1/users/42").contentType(APPLICATION_JSON)
                        .content(registration("Pessoa Exemplo", "new@example.test", PASSWORD, PASSWORD)))
                .andExpect(status().isMethodNotAllowed());
    }

    private UsersEntity fixture(String name, String email) {
        UsersEntity entity = new UsersEntity();
        entity.setFullName(name);
        entity.setEmail(email);
        entity.setPassword(passwordEncoder.encode(PASSWORD));
        entity.setRole(UserRoleEnum.ROLE_USER);
        entity.setCreatedAt(CREATED);
        entity.setUpdatedAt(CREATED);
        entity.setAttempts(0);
        return repository.saveAndFlush(entity);
    }

    private static String registration(String name, String email, String password, String confirmation) {
        return """
                {"fullName":"%s","email":"%s","password":"%s","confirmPassword":"%s"}
                """.formatted(name, email, password, confirmation);
    }

    private static String profile(String name, String email) {
        return """
                {"fullName":"%s","email":"%s"}
                """.formatted(name, email);
    }

    private static String passwordChange(String oldPassword, String password, String confirmation) {
        return """
                {"oldPassword":"%s","newPassword":"%s","confirmNewPassword":"%s"}
                """.formatted(oldPassword, password, confirmation);
    }
}
