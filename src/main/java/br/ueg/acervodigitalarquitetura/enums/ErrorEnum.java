package br.ueg.acervodigitalarquitetura.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    GENERAL(400, "Unknown Error!"),
    NOT_FOUND(404, "Register not found!"),
    PARAMETER_REQUIRED(400, "Mandatory parameter(s) not entered: "),
    IMAGE_EXTENSION_INVALID(400, "Extension image is invalid!"),
    USER_NAME_ALREADY_EXISTS(400, "First name and last name already exists!"),
    USER_EMAIL_ALREADY_EXISTS(400, "Email already exists!");

    private final Integer id;
    private final String message;

    ErrorEnum(Integer id, String message){
        this.id = id;
        this.message = message;
    }
}
