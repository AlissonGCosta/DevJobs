package br.costa.DevJobs.core.domain;

import br.costa.DevJobs.core.domain.enumarated.enumuser.UserRoleEnum;

import java.time.Instant;

public class Users {

    //declarte Useres Variables
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private Instant createdAt;
    private Instant updatedAt;
    private UserRoleEnum role;


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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }

    // constructor basic
    public Users(Long id, String password, String email, String fullName) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.role = UserRoleEnum.ROLE_USER;
    }


}
