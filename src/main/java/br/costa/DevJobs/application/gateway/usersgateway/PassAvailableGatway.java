package br.costa.DevJobs.application.gateway.usersgateway;

public interface PassAvailableGatway {

    Boolean availablePass(String oldPassword, Long id);
}
