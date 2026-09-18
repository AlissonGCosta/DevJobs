package br.costa.DevJobs.core.exception.enums;

public enum ErrorCodeEnum {

    // internal error
    ISE0001("An error occurred while creating the account", "ISE0001"),

    //fullName
    BRN0001("Invalid Name", "BRN0001"),
    BRN0002("Invalid Password", "BRN0002"),
    BRN0003("Passwords are different", "BRN0003"),

    //email
    CML0001("Email already existis", "CML0001"),

    //login
    PIN0002("Incorrect Pin, %d Remaining attempts", "PIN-0002")

    ;

    private String message;
    private String code;



    ErrorCodeEnum(String message, String code) {
        this.message = message;
        this.code = code;
    }

    // getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static String pin002GetMessage(Integer attempt) {

        return String.format(PIN0002.message, attempt);
    }
}
