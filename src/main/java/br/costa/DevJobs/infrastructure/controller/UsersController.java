package br.costa.DevJobs.infrastructure.controller;

import br.costa.DevJobs.infrastructure.config.FindAllConfig;
import br.costa.DevJobs.infrastructure.dto.request.PutUserRequestDto;
import br.costa.DevJobs.infrastructure.dto.request.UsersRequestDto;
import br.costa.DevJobs.infrastructure.dto.response.PutUserResponseDto;
import br.costa.DevJobs.infrastructure.dto.response.UserResponseFindByIdDto;
import br.costa.DevJobs.infrastructure.dto.response.UsersResponseDto;
import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.usecase.userusecase.FindAllUsersUseCase;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import br.costa.DevJobs.usecase.userusecase.PutUsersUseCase;
import br.costa.DevJobs.usecase.userusecase.RegisterAccountUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Validated
public class UsersController {

    private final RegisterAccountUseCase registerAccountUseCase;
    private final FindByIdUsersUseCase findByIdUsersUseCase;
    private final PutUsersUseCase putUsersUseCase;


    private final UserMapper userMapper;
    private final FindAllConfig  findAllConfig;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsersResponseDto create(@RequestBody @Valid UsersRequestDto dto) {
        registerAccountUseCase.create(userMapper.RequestDtoToUsers(dto));
        return userMapper.userRequestToUserResponse(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseFindByIdDto findById(@PathVariable Long id) {
       return userMapper.UserDomainToUserResponseFindById(findByIdUsersUseCase.findById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseFindByIdDto> findAll() {
        return findAllConfig.findAll();
    }

    @PutMapping("/{id}")
    public PutUserResponseDto update(@PathVariable Long id, @RequestBody @Valid PutUserRequestDto dto) {
        return userMapper.UsersDomaintoPutResponseDto(putUsersUseCase.putUsers(userMapper.PutUsersRequestDtoToUsers(dto), id));
    }
}
