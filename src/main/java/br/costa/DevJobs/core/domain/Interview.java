package br.costa.DevJobs.core.domain;

import br.costa.DevJobs.core.domain.enumarated.enumInterview.SituationInterview;
import br.costa.DevJobs.core.domain.enumarated.enumInterview.TypeInterview;
import br.costa.DevJobs.core.domain.enumarated.enumjobapplication.StatusApplicationEnum;

import java.time.Instant;
import java.util.UUID;

public class Interview {


    // variables
    private UUID id;
    private String date;
    private String hour;
    private TypeInterview typeInterview;
    private SituationInterview situationInterview;
    private String InterviewerName;
    private String linkAdress;
    private String observation;
    private Instant createdAt;
    private JobApplication application;

    // getters and setters


    public JobApplication getApplication() {
        return application;
    }

    public void setApplication(JobApplication application) {
        this.application = application;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public TypeInterview getTypeInterview() {
        return typeInterview;
    }

    public void setTypeInterview(TypeInterview typeInterview) {
        this.typeInterview = typeInterview;
    }

    public SituationInterview getSituationInterview() {
        return situationInterview;
    }

    public void setSituationInterview(SituationInterview situationInterview) {
        this.situationInterview = situationInterview;
    }

    public String getInterviewerName() {
        return InterviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        InterviewerName = interviewerName;
    }

    public String getLinkAdress() {
        return linkAdress;
    }

    public void setLinkAdress(String linkAdress) {
        this.linkAdress = linkAdress;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // constructor
    public Interview(String linkAdress,
                     String observation,
                     String interviewerName,
                     String hour,
                     String date,
                     UUID id,
                     JobApplication application,
                     TypeInterview typeInterview) {
        this.linkAdress = linkAdress;
        this.observation = observation;
        this.InterviewerName = interviewerName;
        this.hour = hour;
        this.date = date;
        this.id = id;
        this.createdAt = Instant.now();
        this.application = application;
        this.situationInterview = SituationInterview.Scheduled;
        this.typeInterview = typeInterview;
    }
}
