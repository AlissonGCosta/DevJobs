package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.application.persistence.user.PostUserPersistence;
import br.costa.DevJobs.application.persistence.user.UpdatePassUserPersistence;
import br.costa.DevJobs.application.persistence.user.UpdateUserPersistence;
import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;
import br.costa.DevJobs.core.exception.usersexception.IdFoundAvailableException;
import br.costa.DevJobs.core.exception.usersexception.PassAvailableException;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.config.PasswordConfig;
import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServicesTest {
    @Mock UsersRepository repository;
    private final PasswordConfig passwordConfig = new PasswordConfig();
    private final UserMapper mapper = new UserMapper();
    private static final Instant CREATED = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void registrationHashesPasswordAndReturnsTheGeneratedId() {
        when(repository.save(any(UsersEntity.class))).thenAnswer(invocation -> {
            UsersEntity entity = invocation.getArgument(0);
            entity.setId(42L);
            return entity;
        });
        var data = new PostUserPersistence("Pessoa Exemplo", "person@example.test",
                "password123", UserRoleEnum.ROLE_USER, CREATED, CREATED, 0);
        var service = new UserRegisterService(repository, new CreateUserEntityService(passwordConfig));

        var result = service.registerUser(data);

        var saved = ArgumentCaptor.forClass(UsersEntity.class);
        verify(repository).save(saved.capture());
        assertAll(
                () -> assertEquals(42L, result.id()),
                () -> assertEquals("person@example.test", result.email()),
                () -> assertEquals("Pessoa Exemplo", result.fullName()),
                () -> assertNotEquals("password123", saved.getValue().getPassword()),
                () -> assertTrue(passwordConfig.passwordEncoder().matches("password123",
                        saved.getValue().getPassword()))
        );
    }

    @Test
    void registrationPropagatesRepositoryFailure() {
        var failure = new IllegalStateException("simulated storage failure");
        when(repository.save(any())).thenThrow(failure);
        var data = new PostUserPersistence("Pessoa Exemplo", "person@example.test",
                "password123", UserRoleEnum.ROLE_USER, CREATED, CREATED, 0);
        var service = new UserRegisterService(repository, new CreateUserEntityService(passwordConfig));
        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.registerUser(data)));
    }

    @Test
    void passwordChangeRehashesUpdatesDateAndPreservesOtherUserFields() {

        UpdatePassUserPersistence pass = new UpdatePassUserPersistence(
                "newPassword123"
        );

        UsersEntity entity = user();
        String previousHash = entity.getPassword();
        when(repository.findById(42L)).thenReturn(Optional.of(entity));

        new PatchPasswordUserService(repository, passwordConfig).patchPass(pass, 42L);

        verify(repository).save(entity);
        assertAll(
                () -> assertNotEquals(previousHash, entity.getPassword()),
                () -> assertTrue(passwordConfig.passwordEncoder().matches("newPassword123", entity.getPassword())),
                () -> assertFalse(passwordConfig.passwordEncoder().matches("oldPassword123", entity.getPassword())),
                () -> assertTrue(entity.getUpdatedAt().isAfter(CREATED)),
                () -> assertEquals(CREATED, entity.getCreatedAt()),
                () -> assertEquals("person@example.test", entity.getEmail()),
                () -> assertEquals(UserRoleEnum.ROLE_USER, entity.getRole())
        );
    }

    @Test
    void passwordChangeRejectsUserRemovedBeforeSaving() {

        UpdatePassUserPersistence pass = new UpdatePassUserPersistence(
                "newPassword123"
        );

        when(repository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(IdFoundAvailableException.class,
                () -> new PatchPasswordUserService(repository, passwordConfig).patchPass(pass, 42L));
        verify(repository, never()).save(any());
    }

    @Test
    void passwordChangeDoesNotSwallowRepositoryFailure() {

        UpdatePassUserPersistence pass = new UpdatePassUserPersistence(
                "newPassword123"
        );

        when(repository.findById(42L)).thenReturn(Optional.of(user()));
        var failure = new IllegalStateException("simulated storage failure");
        when(repository.save(any())).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> new PatchPasswordUserService(repository, passwordConfig).patchPass(pass, 42L)));
    }

    @Test
    void oldPasswordIsCheckedAgainstTheStoredHash() {
        when(repository.findById(42L)).thenReturn(Optional.of(user()));
        assertDoesNotThrow(() -> new PassAvailableService(passwordConfig, repository)
                .availablePass("oldPassword123", 42L));
        verify(repository, never()).save(any());
    }

    @Test
    void wrongOldPasswordIsRejectedWithoutSaving() {
        when(repository.findById(42L)).thenReturn(Optional.of(user()));
        assertThrows(PassAvailableException.class, () ->
                new PassAvailableService(passwordConfig, repository).availablePass("wrongPassword", 42L));
        verify(repository, never()).save(any());
    }

    @Test
    void passwordCheckRejectsMissingUser() {
        when(repository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(IdFoundAvailableException.class, () ->
                new PassAvailableService(passwordConfig, repository).availablePass("oldPassword123", 42L));
    }

    @Test
    void profileUpdatePreservesPasswordRoleAndCreationDate() {
        UsersEntity entity = user();
        String previousHash = entity.getPassword();
        when(repository.findById(42L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        var result = new PutUsersService(repository, mapper)
                .putUsers(new UpdateUserPersistence("updated@example.test", "Pessoa Atualizada"), 42L);

        assertAll(
                () -> assertEquals("updated@example.test", entity.getEmail()),
                () -> assertEquals("Pessoa Atualizada", entity.getFullName()),
                () -> assertEquals(previousHash, entity.getPassword()),
                () -> assertEquals(CREATED, entity.getCreatedAt()),
                () -> assertEquals(UserRoleEnum.ROLE_USER, entity.getRole()),
                () -> assertTrue(entity.getUpdatedAt().isAfter(CREATED)),
                () -> assertEquals(entity.getUpdatedAt(), result.updatedAt()),
                () -> assertEquals(entity.getEmail(), result.email()),
                () -> assertEquals(entity.getFullName(), result.fullName())
        );
    }

    @Test
    void profileUpdateRejectsMissingUserWithoutSaving() {
        when(repository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(IdFoundAvailableException.class, () -> new PutUsersService(repository, mapper)
                .putUsers(new UpdateUserPersistence("updated@example.test", "Pessoa Atualizada"), 42L));
        verify(repository, never()).save(any());
    }

    @Test
    void listMapsEntityFieldsWithoutReturningCredentialData() {
        when(repository.findAll()).thenReturn(List.of(user()));
        var result = new FindAllUsersService(repository).findAll();
        assertEquals(1, result.size());
        assertAll(
                () -> assertEquals(42L, result.getFirst().id()),
                () -> assertEquals("Pessoa Exemplo", result.getFirst().fullName()),
                () -> assertEquals("person@example.test", result.getFirst().email()),
                () -> assertEquals(CREATED, result.getFirst().createdAt())
        );
    }

    @Test
    void listWithoutUsersReturnsAnEmptyList() {
        when(repository.findAll()).thenReturn(List.of());
        assertTrue(new FindAllUsersService(repository).findAll().isEmpty());
    }

    private UsersEntity user() {
        UsersEntity entity = new UsersEntity();
        entity.setId(42L);
        entity.setFullName("Pessoa Exemplo");
        entity.setEmail("person@example.test");
        entity.setPassword(passwordConfig.passwordEncoder().encode("oldPassword123"));
        entity.setRole(UserRoleEnum.ROLE_USER);
        entity.setCreatedAt(CREATED);
        entity.setUpdatedAt(CREATED);
        entity.setAttempts(0);
        return entity;
    }
}
