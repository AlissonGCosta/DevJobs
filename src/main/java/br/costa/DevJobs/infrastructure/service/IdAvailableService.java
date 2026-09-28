package br.costa.DevJobs.infrastructure.service;

import br.costa.DevJobs.application.gateway.usersgateway.IdAvailableGateway;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdAvailableService implements IdAvailableGateway {

    private final UsersRepository usersRepository;

    @Override
    public Boolean idAvailable(Long id) {
        return usersRepository.existsById(id);
    }
}
