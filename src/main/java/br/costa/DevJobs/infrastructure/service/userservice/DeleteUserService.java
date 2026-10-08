package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.application.gateway.usersgateway.DeleteUserGateway;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserGateway {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }
}
