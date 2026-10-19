package br.costa.DevJobs.infrastructure.mappers;

import br.costa.DevJobs.application.command.users.PatchUserPassCommand;
import br.costa.DevJobs.application.command.users.RegisterUserRequestCommand;
import br.costa.DevJobs.application.command.users.UpdateUserCommand;
import br.costa.DevJobs.application.result.users.FindAllUsersResult;
import br.costa.DevJobs.application.result.users.RegisterUserResult;
import br.costa.DevJobs.application.result.users.UpdateUserResult;
import br.costa.DevJobs.application.result.users.UserDetailsResult;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.PatchUserPassRequestDto;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.PutUserRequestDto;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.UsersRequestDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.FindAllResponseDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.PutUserResponseDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.UserResponseFindByIdDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.UsersResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class UserMapper {

    public RegisterUserRequestCommand RequestDtoToUsersComand(UsersRequestDto dto) {
        return new RegisterUserRequestCommand(
                dto.fullName(),
                dto.email(),
                dto.password(),
                dto.confirmPassword()
        );
    }

    public UsersResponseDto RegisterUserToUserResponse(RegisterUserResult dto) {
        return new UsersResponseDto(
                dto.id(),
                dto.fullName(),
                dto.email()
        );
    }

    public UpdateUserResult userEntityToUpdateUserResult(UsersEntity entity){

        return new UpdateUserResult(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getRole()
        );
    }

    public UserResponseFindByIdDto userDetailsResultToUserResponseFindById(UserDetailsResult user) {
        return new UserResponseFindByIdDto(
                user.id(),
                user.fullName(),
                user.email(),
                user.createdAt(),
                user.role()
        );
    }

    public UpdateUserCommand putUsersRequestDtoToUsersComand(PutUserRequestDto dto) {
        return new UpdateUserCommand(
                dto.fullName(),
                dto.email()
        );
    }

    public PutUserResponseDto UpdateUserResulttoPutResponseDto(UpdateUserResult user) {
        return new PutUserResponseDto(
                user.id(),
                user.email(),
                user.fullName(),
                user.updatedAt()
        );
    }

    public PatchUserPassCommand patchPassRequestToPatchPassCommand(PatchUserPassRequestDto dto) {
        return new PatchUserPassCommand(
                dto.oldPassword(),
                dto.newPassword(),
                dto.confirmNewPassword()
        );
    }

    public FindAllResponseDto findAllResultToFindAllResponse(FindAllUsersResult user) {
        return new FindAllResponseDto(
                user.id(),
                user.fullName(),
                user.email(),
                user.createdAt(),
                user.role()
        );
    }


}
