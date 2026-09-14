package br.costa.DevJobs.core.domain;

import br.costa.DevJobs.core.domain.enumarated.enumhistoryjobapplication.HistoryJobApplicationEnum;

import java.util.UUID;

public class HistoryJobApplication {
    private UUID uuid;
    private HistoryJobApplicationEnum typeJobApplication;
    private HistoryJobApplicationEnum beforeTypeJobApplication;
    private HistoryJobApplicationEnum newTypeJobApplication;
    private String description;
    private String hour;
    private String date;
    private JobApplication jobApplication;

    // getter and setters

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HistoryJobApplicationEnum getNewTypeJobApplication() {
        return newTypeJobApplication;
    }

    public void setNewTypeJobApplication(HistoryJobApplicationEnum newTypeJobApplication) {
        this.newTypeJobApplication = newTypeJobApplication;
    }

    public HistoryJobApplicationEnum getBeforeTypeJobApplication() {
        return beforeTypeJobApplication;
    }

    public void setBeforeTypeJobApplication(HistoryJobApplicationEnum beforeTypeJobApplication) {
        this.beforeTypeJobApplication = beforeTypeJobApplication;
    }

    public HistoryJobApplicationEnum getTypeJobApplication() {
        return typeJobApplication;
    }

    public void setTypeJobApplication(HistoryJobApplicationEnum typeJobApplication) {
        this.typeJobApplication = typeJobApplication;
    }

    //constructors

    public HistoryJobApplication(JobApplication jobApplication,
                                 String date,
                                 String hour,
                                 String description,
                                 HistoryJobApplicationEnum newTypeJobApplication,
                                 HistoryJobApplicationEnum beforeTypeJobApplication,
                                 HistoryJobApplicationEnum typeJobApplication,
                                 UUID uuid
                                 ) {
        this.jobApplication = jobApplication;
        this.date = date;
        this.hour = hour;
        this.description = description;
        this.newTypeJobApplication = newTypeJobApplication;
        this.beforeTypeJobApplication = beforeTypeJobApplication;
        this.typeJobApplication = typeJobApplication;
        this.uuid = uuid;

    }
}