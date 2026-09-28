package br.costa.DevJobs.infrastructure.controller;

import br.costa.DevJobs.infrastructure.dto.request.UsersRequestDto;
import br.costa.DevJobs.infrastructure.dto.response.UserResponseFindByIdDto;
import br.costa.DevJobs.infrastructure.dto.response.UsersResponseDto;
import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import br.costa.DevJobs.usecase.userusecase.RegisterAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final RegisterAccountUseCase registerAccountUseCase;
    private final FindByIdUsersUseCase findByIdUsersUseCase;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsersResponseDto create(@RequestBody UsersRequestDto dto) {
        registerAccountUseCase.create(userMapper.RequestDtoToUsers(dto));
        return userMapper.userRequestToUserResponse(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseFindByIdDto findById(@PathVariable Long id) {
       return userMapper.UserDomainToUserResponseFindById(findByIdUsersUseCase.findById(id));
    }
}
