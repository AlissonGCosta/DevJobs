package br.costa.DevJobs.infrastructure.mappers;

import br.costa.DevJobs.application.result.users.*;
import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper mapper = new UserMapper();
    private static final Instant CREATED = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant UPDATED = Instant.parse("2026-02-01T00:00:00Z");

    @Test
    void registrationInputKeepsNameEmailAndBothPasswords() {
        var command = mapper.RequestDtoToUsersComand(new UsersRequestDto(
                "Pessoa Exemplo", "person@example.test", "password123", "confirmation123"));
        assertAll(
                () -> assertEquals("Pessoa Exemplo", command.fullName()),
                () -> assertEquals("person@example.test", command.email()),
                () -> assertEquals("password123", command.password()),
                () -> assertEquals("confirmation123", command.confirmPassword())
        );
    }

    @Test
    void registrationResponseUsesStoredIdNameAndEmail() {
        var dto = mapper.RegisterUserToUserResponse(
                new RegisterUserResult(42L, "person@example.test", "Pessoa Exemplo"));
        assertAll(
                () -> assertEquals(42L, dto.id()),
                () -> assertEquals("Pessoa Exemplo", dto.fullName()),
                () -> assertEquals("person@example.test", dto.email())
        );
    }

    @Test
    void updateInputDoesNotSwapNameAndEmail() {
        var command = mapper.putUsersRequestDtoToUsersComand(
                new PutUserRequestDto("Pessoa Atualizada", "updated@example.test"));
        assertAll(
                () -> assertEquals("Pessoa Atualizada", command.fullName()),
                () -> assertEquals("updated@example.test", command.email())
        );
    }

    @Test
    void updateResultUsesCorrectFieldsAndUpdateDate() {
        UsersEntity entity = new UsersEntity();
        entity.setId(42L);
        entity.setFullName("Pessoa Atualizada");
        entity.setEmail("updated@example.test");
        entity.setCreatedAt(CREATED);
        entity.setUpdatedAt(UPDATED);
        entity.setRole(UserRoleEnum.ROLE_USER);

        var result = mapper.userEntityToUpdateUserResult(entity);
        assertAll(
                () -> assertEquals(42L, result.id()),
                () -> assertEquals("Pessoa Atualizada", result.fullName()),
                () -> assertEquals("updated@example.test", result.email()),
                () -> assertEquals(UPDATED, result.updatedAt()),
                () -> assertEquals(UserRoleEnum.ROLE_USER, result.role())
        );
    }

    @Test
    void updateResponseDoesNotSwapNameAndEmail() {
        var dto = mapper.UpdateUserResulttoPutResponseDto(
                new UpdateUserResult(42L, "updated@example.test", "Pessoa Atualizada",
                        UPDATED, UserRoleEnum.ROLE_USER));
        assertAll(
                () -> assertEquals(42L, dto.id()),
                () -> assertEquals("Pessoa Atualizada", dto.fullName()),
                () -> assertEquals("updated@example.test", dto.email()),
                () -> assertEquals(UPDATED, dto.updatedAt())
        );
    }

    @Test
    void individualResponseContainsOnlyThePublicUserFields() {
        var dto = mapper.userDetailsResultToUserResponseFindById(
                new UserDetailsResult(42L, "Pessoa Exemplo", "person@example.test",
                        CREATED, UserRoleEnum.ROLE_USER));
        assertAll(
                () -> assertEquals(42L, dto.id()),
                () -> assertEquals("Pessoa Exemplo", dto.fullName()),
                () -> assertEquals("person@example.test", dto.email()),
                () -> assertEquals(CREATED, dto.createdAt()),
                () -> assertEquals(UserRoleEnum.ROLE_USER, dto.role())
        );
    }

    @Test
    void listResponsePreservesAllPublicFields() {
        var dto = mapper.findAllResultToFindAllResponse(
                new FindAllUsersResult(42L, "Pessoa Exemplo", "person@example.test",
                        CREATED, UserRoleEnum.ROLE_USER));
        assertAll(
                () -> assertEquals(42L, dto.id()),
                () -> assertEquals("Pessoa Exemplo", dto.fullName()),
                () -> assertEquals("person@example.test", dto.email()),
                () -> assertEquals(CREATED, dto.createdAt()),
                () -> assertEquals(UserRoleEnum.ROLE_USER, dto.role())
        );
    }

    @Test
    void passwordCommandKeepsOldNewAndConfirmationSeparate() {
        var command = mapper.patchPassRequestToPatchPassCommand(
                new PatchUserPassRequestDto("oldPassword123", "newPassword123", "confirmation123"));
        assertAll(
                () -> assertEquals("oldPassword123", command.oldPassword()),
                () -> assertEquals("newPassword123", command.newPassword()),
                () -> assertEquals("confirmation123", command.confirmNewPassword())
        );
    }
}
