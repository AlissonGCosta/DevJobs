package br.costa.DevJobs.infrastructure.mappers;

import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.config.PasswordConfig;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.PatchUserPassRequestDto;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.PutUserRequestDto;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.UsersRequestDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.PutUserResponseDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.UserResponseFindByIdDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.UsersResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class UserMapper {

    private final PasswordConfig passwordConfig;

    public Users RequestDtoToUsers(UsersRequestDto dto) {
        return new Users(
                dto.fullName(),
                dto.email(),
                dto.password(),
                dto.confirmPassword()
        );
    }

    public UsersResponseDto userRequestToUserResponse(UsersRequestDto dto) {
        return new UsersResponseDto(
                dto.fullName(),
                dto.email()
        );
    }

    public Users userEntityToDomain(UsersEntity entity){

        return new Users(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getRole()
        );
    }

    public UserResponseFindByIdDto userDomainToUserResponseFindById(Users user) {
        return new UserResponseFindByIdDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getRole()
        );
    }

    public Users putUsersRequestDtoToUsers(PutUserRequestDto dto) {
        return new Users(
                dto.fullName(),
                dto.email()
        );
    }

    public PutUserResponseDto usersDomaintoPutResponseDto(Users user) {
        return new PutUserResponseDto(
                user.getFullName(),
                user.getEmail(),
                user.getUpdatedAt()
        );
    }

    public Users patchPassRequestToUsers(PatchUserPassRequestDto dto) {
        return new Users(
                dto.oldPassword(),
                dto.newPassword(),
                dto.confirmNewPassword()
        );
    }


}
