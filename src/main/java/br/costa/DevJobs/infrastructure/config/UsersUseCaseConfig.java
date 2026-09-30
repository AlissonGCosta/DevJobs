package br.costa.DevJobs.infrastructure.config;

import br.costa.DevJobs.application.gateway.usersgateway.*;
import br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl.*;
import br.costa.DevJobs.usecase.userusecase.*;
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
    public FindAllUsersUseCase findAllUsersUseCase(FindAllUsersGateway gateway){
        return new FindAllUsersCaseImpl(gateway);
    }

    @Bean
    public PutUsersUseCase putUsersUseCase(PutUsersGateway gateway,
                                           EmailAvailableUseCase emailAvailableUseCase,
                                           FindByIdUsersUseCase findByIdUsersUseCase) {
        return new PutUsersUseCaseImpl(gateway, emailAvailableUseCase,  findByIdUsersUseCase);
    }

}
