package br.costa.DevJobs.core.domain;

import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;
import br.costa.DevJobs.core.exception.InvalidPasswordException;
import br.costa.DevJobs.core.exception.ValidateFullNameException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;

import java.time.Instant;

public class Users {

    //declarte Useres Variables
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;
    private UserRoleEnum role;
    private Instant createdAt;
    private Instant updatedAt;
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
    public Users(
            String fullName,
            String email,
            String password,
            String confirmPassword

            ) {


        this.email = email;
        validateFullName(fullName);
        validatePassword(password);
        validateEqualsPassword(confirmPassword);
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.role = UserRoleEnum.ROLE_USER;
        this.attempt = 0;
    }

    public Users(Long id ,
                 String fullName,
                 String email,
                 Instant createdAt,
                 UserRoleEnum role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
        this.role = role;
    }

    public Users(
                 String fullName,
                 String email){
        this.fullName = fullName;
        this.email = email;

    }

    // validators
    private void validateFullName(String fullName) {
        if (fullName.length() <= 5 || fullName.length() > 100) {
            throw new ValidateFullNameException(ErrorCodeEnum.VPN0001.getMessage(), ErrorCodeEnum.VPN0001.getCode());
        }

        this.fullName = fullName;
    }

    private void validatePassword(String password) {
        if (password.length() < 8 || password.length() > 64) {
            throw new InvalidPasswordException(ErrorCodeEnum.IPN0001.getMessage(), ErrorCodeEnum.IPN0001.getCode());
        }

        this.password = password;
    }

    private void validateEqualsPassword(String confirmPassword) {
        if(!confirmPassword.equals(getPassword())) {
            throw new InvalidPasswordException(ErrorCodeEnum.IPN0002.getMessage(), ErrorCodeEnum.IPN0002.getCode());
        }

        this.confirmPassword = confirmPassword;
    }


}
