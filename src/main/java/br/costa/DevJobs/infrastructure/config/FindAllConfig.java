package br.costa.DevJobs.infrastructure.config;

import br.costa.DevJobs.infrastructure.dto.response.UserResponseFindByIdDto;
import br.costa.DevJobs.usecase.userusecase.FindAllUsersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllConfig {

    private final FindAllUsersUseCase findAllUsersUseCase;

    public List<UserResponseFindByIdDto> findAll(){

        return findAllUsersUseCase.findAll().stream().map( users
                                -> new UserResponseFindByIdDto(
                                users.getId(),
                                users.getFullName(),
                                users.getEmail(),
                                users.getCreatedAt(),
                                users.getRole()
                        )
                )
                .toList();
    }
}
