package br.costa.DevJobs.core.domain;

import java.time.Instant;

public class Users {

    //declarte Useres Variables
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private Instant createdAt;

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // constructor basic
    public Users(Long id, String password, String email, String fullName) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.createdAt = Instant.now();
    }


}
