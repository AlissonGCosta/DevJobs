package br.costa.DevJobs.infrastructure.controller;

import br.costa.DevJobs.infrastructure.dto.response.userresponse.FindAllResponseDto;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.PatchUserPassRequestDto;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.PutUserRequestDto;
import br.costa.DevJobs.infrastructure.dto.request.userrequest.UsersRequestDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.PutUserResponseDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.UserResponseFindByIdDto;
import br.costa.DevJobs.infrastructure.dto.response.userresponse.UsersResponseDto;
import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.usecase.userusecase.*;
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
    private final PatchPassUseCase patchPassUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;


    private final UserMapper userMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsersResponseDto create(@RequestBody @Valid UsersRequestDto dto) {
        return userMapper.RegisterUserToUserResponse(registerAccountUseCase.create(userMapper.RequestDtoToUsersComand(dto)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseFindByIdDto findById(@PathVariable Long id) {
       return userMapper.userDetailsResultToUserResponseFindById(findByIdUsersUseCase.findById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FindAllResponseDto> findAll() {
        return findAllUsersUseCase.findAll().stream().map( userMapper::findAllResultToFindAllResponse)
                .toList();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PutUserResponseDto update(@PathVariable Long id, @RequestBody @Valid PutUserRequestDto dto) {
        return userMapper.UpdateUserResulttoPutResponseDto(
                putUsersUseCase.putUsers(userMapper.putUsersRequestDtoToUsersComand(dto), id)
        );
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePass(@PathVariable Long id, @RequestBody @Valid PatchUserPassRequestDto dto) {

        patchPassUseCase.patchPass(userMapper.patchPassRequestToPatchPassCommand(dto), id);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        deleteUserUseCase.delete(id);
    }
}
