package br.costa.DevJobs.core.domain;

import br.costa.DevJobs.core.domain.enumarated.enumjobapplication.StatusApplicationEnum;

import java.time.Instant;
import java.util.UUID;

public class JobApplication {

    // declarate variables
    private UUID id;
    private String dateApplication;
    private String channelUtility;
    private String nameRecruter;
    private String contactRecruter;
    private String observation;
    private String lastUpdated;
    private Users users;
    private Instant createdAt;
    private Instant updatedAt;
    private StatusApplicationEnum status;

    // getters and setters

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StatusApplicationEnum getStatus() {
        return status;
    }

    public void setStatus(StatusApplicationEnum status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDateApplication() {
        return dateApplication;
    }

    public void setDateApplication(String dateApplication) {
        this.dateApplication = dateApplication;
    }

    public String getChannelUtility() {
        return channelUtility;
    }

    public void setChannelUtility(String channelUtility) {
        this.channelUtility = channelUtility;
    }

    public String getNameRecruter() {
        return nameRecruter;
    }

    public void setNameRecruter(String nameRecruter) {
        this.nameRecruter = nameRecruter;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getContactRecruter() {
        return contactRecruter;
    }

    public void setContactRecruter(String contactRecruter) {
        this.contactRecruter = contactRecruter;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // constructor

    public JobApplication(UUID id,
                          String dateApplication,
                          String channelUtility,
                          String nameRecruter,
                          String contactRecruter,
                          String observation,
                          String lastUpdated,
                          Users users) {
        this.id = id;
        this.dateApplication = dateApplication;
        this.channelUtility = channelUtility;
        this.nameRecruter = nameRecruter;
        this.contactRecruter = contactRecruter;
        this.observation = observation;
        this.lastUpdated = lastUpdated;
        this.users = users;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.status = StatusApplicationEnum.APPLICATION_SUBMITTED;

    }
}
