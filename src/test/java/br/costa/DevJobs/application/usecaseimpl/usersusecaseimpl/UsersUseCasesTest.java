package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.command.users.PatchUserPassCommand;
import br.costa.DevJobs.application.command.users.RegisterUserRequestCommand;
import br.costa.DevJobs.application.command.users.UpdateUserCommand;
import br.costa.DevJobs.application.gateway.usersgateway.*;
import br.costa.DevJobs.application.persistence.user.PostUserPersistence;
import br.costa.DevJobs.application.persistence.user.UpdatePassUserPersistence;
import br.costa.DevJobs.application.persistence.user.UpdateUserPersistence;
import br.costa.DevJobs.application.result.users.*;
import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;
import br.costa.DevJobs.core.exception.usersexception.*;
import br.costa.DevJobs.usecase.userusecase.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersUseCasesTest {
    @Mock RegisterUserGateway registerGateway;
    @Mock PutUsersGateway updateGateway;
    @Mock PatchPassUsersGateway passwordGateway;
    @Mock DeleteUserGateway deleteGateway;
    @Mock EmailAvailableUseCase emailExists;
    @Mock IdAvailableUseCase idExists;
    @Mock FindByIdUsersUseCase findUser;
    @Mock PassAvailableUseCase checkOldPassword;

    private static final Long ID = 42L;
    private static final Instant CREATED = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void registrationSendsValidatedUserAndReturnsStoredResult() {
        var expected = new RegisterUserResult(ID, "new@example.test", "Pessoa Exemplo");
        when(registerGateway.registerUser(any())).thenReturn(expected);
        var useCase = new RegisterAccountUseCaseImpl(registerGateway, emailExists);

        assertSame(expected, useCase.create(registration()));
        var data = ArgumentCaptor.forClass(PostUserPersistence.class);
        verify(registerGateway).registerUser(data.capture());
        assertAll(
                () -> assertEquals("Pessoa Exemplo", data.getValue().fullName()),
                () -> assertEquals("new@example.test", data.getValue().email()),
                () -> assertEquals("password123", data.getValue().password()),
                () -> assertEquals(UserRoleEnum.ROLE_USER, data.getValue().role()),
                () -> assertEquals(0, data.getValue().attempts()),
                () -> assertNotNull(data.getValue().createdAt())
        );
    }

    @Test
    void registrationRejectsExistingEmailBeforeSaving() {
        when(emailExists.emailAvailable("new@example.test")).thenReturn(true);
        var useCase = new RegisterAccountUseCaseImpl(registerGateway, emailExists);
        assertThrows(EmailAvaliableException.class, () -> useCase.create(registration()));
        verifyNoInteractions(registerGateway);
    }

    @Test
    void registrationRejectsInvalidDomainInputBeforeCallingGateways() {
        var input = new RegisterUserRequestCommand("Pessoa Exemplo", "new@example.test", "short", "short");
        var useCase = new RegisterAccountUseCaseImpl(registerGateway, emailExists);
        assertThrows(InvalidPasswordException.class, () -> useCase.create(input));
        verifyNoInteractions(registerGateway, emailExists);
    }

    @Test
    void registrationPropagatesPersistenceFailure() {
        var failure = new IllegalStateException("simulated storage failure");
        when(registerGateway.registerUser(any())).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> new RegisterAccountUseCaseImpl(registerGateway, emailExists).create(registration())));
    }

    @Test
    void updateAllowsOwnEmailWithoutCheckingItAsDuplicate() {
        when(findUser.findById(ID)).thenReturn(existingUser());
        update().putUsers(new UpdateUserCommand("Pessoa Atualizada", "original@example.test"), ID);
        verifyNoInteractions(emailExists);
        verify(updateGateway).putUsers(any(), eq(ID));
    }

    @Test
    void updatePreservesNameAndEmailPositionsWhenBuildingPersistenceData() {
        when(findUser.findById(ID)).thenReturn(existingUser());
        var expected = new UpdateUserResult(ID, "available@example.test", "Pessoa Atualizada",
                Instant.now(), UserRoleEnum.ROLE_USER);
        when(updateGateway.putUsers(any(), eq(ID))).thenReturn(expected);

        assertSame(expected, update().putUsers(
                new UpdateUserCommand("Pessoa Atualizada", "available@example.test"), ID));

        var data = ArgumentCaptor.forClass(UpdateUserPersistence.class);
        verify(updateGateway).putUsers(data.capture(), eq(ID));
        assertAll(
                () -> assertEquals("Pessoa Atualizada", data.getValue().fullName()),
                () -> assertEquals("available@example.test", data.getValue().email())
        );
        verify(emailExists).emailAvailable("available@example.test");
    }

    @Test
    void updateRejectsAnotherUsersEmailBeforeSaving() {
        when(findUser.findById(ID)).thenReturn(existingUser());
        when(emailExists.emailAvailable("occupied@example.test")).thenReturn(true);
        assertThrows(EmailAvaliableException.class, () -> update().putUsers(
                new UpdateUserCommand("Pessoa Atualizada", "occupied@example.test"), ID));
        verifyNoInteractions(updateGateway);
    }

    @Test
    void updateStopsWhenUserDoesNotExist() {
        when(findUser.findById(ID)).thenThrow(new IdFoundAvailableException("missing", "IAI0001"));
        assertThrows(IdFoundAvailableException.class, () -> update().putUsers(
                new UpdateUserCommand("Pessoa Atualizada", "available@example.test"), ID));
        verifyNoInteractions(emailExists, updateGateway);
    }

    @Test
    void updateRejectsInvalidNameBeforeSaving() {
        when(findUser.findById(ID)).thenReturn(existingUser());
        assertThrows(ValidateFullNameException.class, () -> update().putUsers(
                new UpdateUserCommand("Ana", "original@example.test"), ID));
        verifyNoInteractions(updateGateway);
    }

    @Test
    void updatePropagatesStorageFailure() {
        when(findUser.findById(ID)).thenReturn(existingUser());
        var failure = new IllegalStateException("simulated storage failure");
        when(updateGateway.putUsers(any(), eq(ID))).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> update().putUsers(
                new UpdateUserCommand("Pessoa Atualizada", "original@example.test"), ID)));
    }

    @Test
    void passwordChangeChecksOldPasswordThenSendsOnlyNewPasswordAndId() {

        UpdatePassUserPersistence pass = new UpdatePassUserPersistence(
                "newPassword123"
        );

        when(idExists.idAvailable(ID)).thenReturn(true);
        passwordChange().patchPass(passwordCommand("newPassword123", "newPassword123"), ID);
        var order = inOrder(checkOldPassword, passwordGateway);
        order.verify(checkOldPassword).availablePass("oldPassword123", ID);
        order.verify(passwordGateway).patchPass(pass, ID);
    }

    @Test
    void passwordChangeStopsForMissingUser() {
        when(idExists.idAvailable(ID)).thenReturn(false);
        assertThrows(IdFoundAvailableException.class, () ->
                passwordChange().patchPass(passwordCommand("newPassword123", "newPassword123"), ID));
        verifyNoInteractions(checkOldPassword, passwordGateway);
    }

    @Test
    void passwordChangeStopsForWrongOldPassword() {
        when(idExists.idAvailable(ID)).thenReturn(true);
        doThrow(new PassAvailableException("incorrect", "PSS0001"))
                .when(checkOldPassword).availablePass("oldPassword123", ID);
        assertThrows(PassAvailableException.class, () ->
                passwordChange().patchPass(passwordCommand("newPassword123", "newPassword123"), ID));
        verifyNoInteractions(passwordGateway);
    }

    @Test
    void passwordChangeStopsForMismatchedConfirmation() {
        when(idExists.idAvailable(ID)).thenReturn(true);
        assertThrows(PassAvailableException.class, () ->
                passwordChange().patchPass(passwordCommand("newPassword123", "differentPassword"), ID));
        verifyNoInteractions(passwordGateway);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 7, 65})
    void passwordChangeRejectsLengthOutsideDomainPolicy(int length) {
        when(idExists.idAvailable(ID)).thenReturn(true);
        String password = "a".repeat(length);
        assertThrows(InvalidPasswordException.class, () ->
                passwordChange().patchPass(passwordCommand(password, password), ID));
        verifyNoInteractions(passwordGateway);
    }

    @Test
    void passwordChangePropagatesStorageFailure() {

        UpdatePassUserPersistence pass = new UpdatePassUserPersistence(
                "newPassword123"
        );


        when(idExists.idAvailable(ID)).thenReturn(true);
        var failure = new IllegalStateException("simulated storage failure");
        doThrow(failure).when(passwordGateway).patchPass(pass, ID);
        assertSame(failure, assertThrows(IllegalStateException.class, () ->
                passwordChange().patchPass(passwordCommand("newPassword123", "newPassword123"), ID)));
    }

    @Test
    void deleteExistingUser() {
        when(idExists.idAvailable(ID)).thenReturn(true);
        new DeleteUserUseCaseImpl(deleteGateway, idExists).delete(ID);
        verify(deleteGateway).deleteUser(ID);
    }

    @Test
    void deleteRejectsMissingUserBeforeCallingStorage() {
        when(idExists.idAvailable(ID)).thenReturn(false);
        assertThrows(IdFoundAvailableException.class,
                () -> new DeleteUserUseCaseImpl(deleteGateway, idExists).delete(ID));
        verifyNoInteractions(deleteGateway);
    }

    @Test
    void deletePropagatesStorageFailure() {
        when(idExists.idAvailable(ID)).thenReturn(true);
        var failure = new IllegalStateException("simulated storage failure");
        doThrow(failure).when(deleteGateway).deleteUser(ID);
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> new DeleteUserUseCaseImpl(deleteGateway, idExists).delete(ID)));
    }

    private PutUsersUseCaseImpl update() {
        return new PutUsersUseCaseImpl(updateGateway, emailExists, findUser);
    }

    private PatchPassUseCaseImpl passwordChange() {
        return new PatchPassUseCaseImpl(idExists, passwordGateway, checkOldPassword);
    }

    private RegisterUserRequestCommand registration() {
        return new RegisterUserRequestCommand("Pessoa Exemplo", "new@example.test", "password123", "password123");
    }

    private PatchUserPassCommand passwordCommand(String password, String confirmation) {
        return new PatchUserPassCommand("oldPassword123", password, confirmation);
    }

    private UserDetailsResult existingUser() {
        return new UserDetailsResult(ID, "Pessoa Original", "original@example.test", CREATED, UserRoleEnum.ROLE_USER);
    }
}
