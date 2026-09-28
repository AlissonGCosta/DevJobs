package br.costa.DevJobs.infrastructure.config;

import br.costa.DevJobs.application.gateway.usersgateway.EmailAvailableGateway;
import br.costa.DevJobs.application.gateway.usersgateway.FindByIdGateway;
import br.costa.DevJobs.application.gateway.usersgateway.IdAvailableGateway;
import br.costa.DevJobs.application.gateway.usersgateway.RegisterUserGateway;
import br.costa.DevJobs.application.usecaseimpl.usersusecase.EmailAvailableUseCaseImpl;
import br.costa.DevJobs.application.usecaseimpl.usersusecase.FindByIdUsersUseCaseImpl;
import br.costa.DevJobs.application.usecaseimpl.usersusecase.IdAvaliableUseCaseImpl;
import br.costa.DevJobs.application.usecaseimpl.usersusecase.RegisterAccountUseCaseImpl;
import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.usecase.userusecase.EmailAvailableUseCase;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import br.costa.DevJobs.usecase.userusecase.IdAvailableUseCase;
import br.costa.DevJobs.usecase.userusecase.RegisterAccountUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsersUseCaseConfig {

    @Bean
    public EmailAvailableUseCase emailAvailableUseCase(EmailAvailableGateway gateway) {

        return new EmailAvailableUseCaseImpl(gateway);
    }

    @Bean
    public RegisterAccountUseCase registerAccountUseCase(RegisterUserGateway gateway,
                                                         EmailAvailableUseCase useCase) {
        return new RegisterAccountUseCaseImpl(gateway,
                useCase);
    }

    @Bean
    public IdAvailableUseCase idAvailableUseCase(IdAvailableGateway gateway) {
        return new IdAvaliableUseCaseImpl(gateway);
    }

    @Bean
    public FindByIdUsersUseCase findByIdUsersUseCase(FindByIdGateway gateway,
                                                     IdAvailableUseCase useCase) {
        return new FindByIdUsersUseCaseImpl(gateway, useCase);
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }
}
