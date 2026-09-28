package br.costa.DevJobs.core.domain.users;

import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.InvalidPasswordException;
import br.costa.DevJobs.core.exception.ValidateFullNameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsersTest {

    @Test
    void shouldRejectPasswithminor8Characters(){

        String shortPassword = "abc123";

        assertThrows(InvalidPasswordException.class, () -> {
            new Users(
                    null,
                    shortPassword,
                    shortPassword,
                    "test@email.com",
                    "Alisson test"
            );
        });
    }

    @Test
    void shouldRejectBigPassword(){

        String bigPassword = "aseiufgbasiugansgnaiasiasiusaiuuifausaiusgiuasiufgiyuasgfiuygasiufgiausfiuahbsduibasjbcvjxbziubqjasikujauxb";
        assertThrows(InvalidPasswordException.class, () -> {
            new Users(
                    null,
                    bigPassword,
                    bigPassword,
                    "test@email.com",
                    "Alisson test"
            );
        });
    }

    @Test
    void shouldRejectShortName(){

        String password = "teste123456789010101";
        assertThrows(ValidateFullNameException.class, () -> {
            new Users(
                    null,
                    password,
                    password,
                    "test@email.com",
                    "OPA"
            );
        });
    }


    @Test
    void shouldRejectBigName(){

        String password = "teste123456789010101";
        assertThrows(ValidateFullNameException.class, () -> {
            new Users(
                    null,
                    password,
                    password,
                    "test@email.com",
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLIIIIIIIIIIIIIIIIIIIIIIIIIIISSSSSSSSSSSSSSSSSSSSSSSSSSSSOOOOOOOOOOOOOOOOONNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
            );
        });
    }

    @Test
    void shouldRejectdiferentPass(){

        String password = "teste123456789010101";
        assertThrows(InvalidPasswordException.class, () -> {
            new Users(
                    null,
                    password,
                    "teste1234",
                    "test@email.com",
                    "alisson"
            );
        });
    }

    @Test
    void shouldCreateUserWithValidData(){
        String pass = "teste1234";

        Users users = assertDoesNotThrow(() -> new Users(
                null,
                pass,
                pass,
                "test@email.com",
                "Alisson Costa"
        ));
        assertEquals("Alisson Costa", users.getFullName());
        assertEquals("test@email.com", users.getEmail());
        assertEquals(pass, users.getPassword());
        assertNotNull(users.getCreatedAt());

    }
}
