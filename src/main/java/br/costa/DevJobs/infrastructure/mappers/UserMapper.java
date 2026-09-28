package br.costa.DevJobs.infrastructure.mappers;

import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.dto.request.UsersRequestDto;
import br.costa.DevJobs.infrastructure.dto.response.UserResponseFindByIdDto;
import br.costa.DevJobs.infrastructure.dto.response.UsersResponseDto;

public class UserMapper {

    public Users RequestDtoToUsers(UsersRequestDto dto) {
        return new Users(
                dto.fullName(),
                dto.email(),
                dto.password(),
                dto.confirmPassword()
        );
    }

    public UsersEntity userDomaintoUsersEntity(Users user) {
        return new UsersEntity(
                user.getFullName(),
                user.getEmail(),
                user.getPassword(),
                user.getConfirmPassword(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getAttempt()
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

    public UserResponseFindByIdDto UserDomainToUserResponseFindById(Users user) {
        return new UserResponseFindByIdDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getRole()
        );
    }
}
