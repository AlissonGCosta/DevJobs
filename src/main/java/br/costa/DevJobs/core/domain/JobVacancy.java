package br.costa.DevJobs.core.domain;

import br.costa.DevJobs.core.domain.enumjobvacany.EnumJobVacancy;
import br.costa.DevJobs.core.domain.enumjobvacany.ExpirienceLevelEnum;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class JobVacancy {

    // declarete variables
    private UUID id;
    private String title;
    private String nameCompany;
    private String description;
    private EnumJobVacancy modelWork;
    private String wokrAdress;
    private ExpirienceLevelEnum expirienceLevel;
    private String tecnology;
    private BigDecimal salary;
    private String link;
    private String observation;
    private Instant createdAt;
    private Instant updatedAt;
    private Users user;

    // geters and setters


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getTecnology() {
        return tecnology;
    }

    public void setTecnology(String tecnology) {
        this.tecnology = tecnology;
    }

    public ExpirienceLevelEnum getExpirienceLevel() {
        return expirienceLevel;
    }

    public void setExpirienceLevel(ExpirienceLevelEnum expirienceLevel) {
        this.expirienceLevel = expirienceLevel;
    }

    public String getWokrAdress() {
        return wokrAdress;
    }

    public void setWokrAdress(String wokrAdress) {
        this.wokrAdress = wokrAdress;
    }

    public EnumJobVacancy getModelWork() {
        return modelWork;
    }

    public void setModelWork(EnumJobVacancy modelWork) {
        this.modelWork = modelWork;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameCompany() {
        return nameCompany;
    }

    public void setNameCompany(String nameCompany) {
        this.nameCompany = nameCompany;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // constructors

    public JobVacancy(UUID id,
                      String title,
                      String nameCompany,
                      String description,
                      EnumJobVacancy modelWork,
                      String wokrAdress,
                      ExpirienceLevelEnum expirienceLevel,
                      String tecnology,
                      BigDecimal salary,
                      String link,
                      String observation,
                      Users user) {
        this.id = id;
        this.title = title;
        this.nameCompany = nameCompany;
        this.description = description;
        this.modelWork = modelWork;
        this.wokrAdress = wokrAdress;
        this.expirienceLevel = expirienceLevel;
        this.tecnology = tecnology;
        this.salary = salary;
        this.link = link;
        this.observation = observation;
        this.user = user;
        this.createdAt = Instant.now();
    }

}
