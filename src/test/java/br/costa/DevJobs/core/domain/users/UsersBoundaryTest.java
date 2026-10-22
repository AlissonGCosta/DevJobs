package br.costa.DevJobs.core.domain.users;

import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;
import br.costa.DevJobs.core.exception.usersexception.InvalidPasswordException;
import br.costa.DevJobs.core.exception.usersexception.ValidateFullNameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UsersBoundaryTest {

    @ParameterizedTest
    @ValueSource(ints = {8, 64})
    void acceptsPasswordAtBothLengthLimits(int length) {
        String password = "a".repeat(length);
        Users user = new Users("Pessoa Exemplo", "person@example.test", password, password);
        assertEquals(password, user.getPassword());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 7, 65})
    void rejectsPasswordOutsideLengthLimits(int length) {
        String password = "a".repeat(length);
        assertThrows(InvalidPasswordException.class,
                () -> new Users("Pessoa Exemplo", "person@example.test", password, password));
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 100})
    void acceptsNameAtBothLengthLimits(int length) {
        Users user = new Users("a".repeat(length), "person@example.test", "password123", "password123");
        assertEquals(length, user.getFullName().length());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5, 101})
    void rejectsNameOutsideLengthLimits(int length) {
        assertThrows(ValidateFullNameException.class,
                () -> new Users("a".repeat(length), "person@example.test", "password123", "password123"));
    }

    @Test
    void assignsInitialRoleAttemptsAndDates() {
        Users user = new Users("Pessoa Exemplo", "person@example.test", "password123", "password123");
        assertAll(
                () -> assertEquals(UserRoleEnum.ROLE_USER, user.getRole()),
                () -> assertEquals(0, user.getAttempt()),
                () -> assertNotNull(user.getCreatedAt()),
                () -> assertNotNull(user.getUpdatedAt())
        );
    }

    @Test
    void rejectedNameChangePreservesPreviousName() {
        Users user = new Users("Pessoa Exemplo", "person@example.test", "password123", "password123");
        assertThrows(ValidateFullNameException.class, () -> user.setFullName("Ana"));
        assertEquals("Pessoa Exemplo", user.getFullName());
    }

    @Test
    void rejectedPasswordChangePreservesPreviousPassword() {
        Users user = new Users("Pessoa Exemplo", "person@example.test", "password123", "password123");
        assertThrows(InvalidPasswordException.class, () -> user.setPassword("short"));
        assertEquals("password123", user.getPassword());
    }
}
