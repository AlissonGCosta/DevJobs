package br.costa.DevJobs.infrastructure.service;

import br.costa.DevJobs.application.gateway.usersgateway.EmailAvailableGateway;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAvailableService  implements EmailAvailableGateway {

    private final UsersRepository usersRepository;

    @Override
    public Boolean emailAvaliable(String email) {
        return  usersRepository.existsByEmail(email);

    }
}
