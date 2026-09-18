package br.costa.DevJobs.core.domain;

import br.costa.DevJobs.core.domain.enumarated.enumuser.UserRoleEnum;
import br.costa.DevJobs.core.exception.BadRequestException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;

import java.time.Instant;

public class Users {

    //declarte Useres Variables
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;
    private Instant createdAt;
    private Instant updatedAt;
    private UserRoleEnum role;
    private Integer attempt;


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
        validatePassword(password);
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
        validateFullName(fullName);
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

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        validateEqualsPassword(confirmPassword);
    }

    // constructor basic
    public Users(Long id, String password,String confirmPassword, String email, String fullName) {
        this.id = id;
        validatePassword(password);
        validateEqualsPassword(confirmPassword);
        this.email = email;
        validateFullName(fullName);
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.role = UserRoleEnum.ROLE_USER;
        this.attempt = 0;
    }

    // validators

    private void validateFullName(String fullName) {
        if (fullName.length() <= 10 || fullName.length() > 100) {
            throw new BadRequestException(ErrorCodeEnum.BRN0001.getMessage(), ErrorCodeEnum.BRN0001.getCode());
        }

        this.fullName = fullName;
    }

    private void validatePassword(String password) {
        if (password.length() < 15 || password.length() > 64) {
            throw new BadRequestException(ErrorCodeEnum.BRN0002.getMessage(), ErrorCodeEnum.BRN0002.getCode());
        }

        this.password = password;
    }

    private void validateEqualsPassword(String confirmPassword) {
        if(!confirmPassword.equals(getPassword())) {
            throw new BadRequestException(ErrorCodeEnum.BRN0003.getMessage(), ErrorCodeEnum.BRN0003.getCode());
        }

        this.confirmPassword = confirmPassword;
    }


}
