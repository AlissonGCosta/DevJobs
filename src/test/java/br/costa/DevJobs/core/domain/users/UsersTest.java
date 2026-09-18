package br.costa.DevJobs.core.domain.users;

import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsersTest {

    @Test
    void shouldRejectPasswithminor15Characters(){

        String shortPassword = "abc123";

        assertThrows(BadRequestException.class, () -> {
            new Users(
                    null,
                    shortPassword,
                    shortPassword,
                    "test@email.com",
                    "Alisson test"
            );
        });
    }
}
