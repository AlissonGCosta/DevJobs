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
                    "Alisson test",
                    "test@email.com",
                    shortPassword,
                    shortPassword
            );
        });
    }

    @Test
    void shouldRejectBigPassword(){

        String bigPassword = "aseiufgbasiugansgnaiasiasiusaiuuifausaiusgiuasiufgiyuasgfiuygasiufgiausfiuahbsduibasjbcvjxbziubqjasikujauxb";
        assertThrows(InvalidPasswordException.class, () -> {
            new Users(
                    "Alisson test",
                    "test@email.com",
                    bigPassword,
                    bigPassword
            );
        });
    }

    @Test
    void shouldRejectShortName(){

        String password = "teste123456789010101";
        assertThrows(ValidateFullNameException.class, () -> {
            new Users(
                    "OPA",
                    "test@email.com",
                    password,
                    password
            );
        });
    }


    @Test
    void shouldRejectBigName(){

        String password = "teste123456789010101";
        assertThrows(ValidateFullNameException.class, () -> {
            new Users(
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLIIIIIIIIIIIIIIIIIIIIIIIIIIISSSSSSSSSSSSSSSSSSSSSSSSSSSSOOOOOOOOOOOOOOOOONNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",
                    "test@mail.com",
                    password,
                    password
            );
        });
    }

    @Test
    void shouldRejectdiferentPass(){

        String password = "teste123456789010101";
        assertThrows(InvalidPasswordException.class, () -> {
            new Users(
                    "Alisson",
                    "test@email.com",
                    password,
                    "teste12345"
            );
        });
    }

    @Test
    void shouldCreateUserWithValidData(){
        String pass = "teste1234";

        Users users = assertDoesNotThrow(() -> new Users(
                "Alisson Costa",
                "test@email.com",
                pass,
                pass
        ));
        assertEquals("Alisson Costa", users.getFullName());
        assertEquals("test@email.com", users.getEmail());
        assertEquals(pass, users.getPassword());
        assertNotNull(users.getCreatedAt());

    }
}
