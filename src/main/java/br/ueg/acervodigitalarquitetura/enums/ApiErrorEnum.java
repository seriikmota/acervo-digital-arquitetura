package br.ueg.acervodigitalarquitetura.enums;

import lombok.Getter;

@Getter
public enum ApiErrorEnum implements MessageCode {
    GENERAL("MEA001", MessageType.ERROR),
    NOT_FOUND("MEA002", MessageType.ERROR),
    PARAMETER_REQUIRED("MEA003", MessageType.ERROR),
    LOGIN_INVALID("MEA004", MessageType.ERROR),
    USER_PASSWORD_NOT_MATCH("MEA005", MessageType.ERROR),
    INACTIVE_USER("MEA006", MessageType.ERROR),
    INVALID_TOKEN("MEA007", MessageType.ERROR),
    EXPIRED_TOKEN("MEA008", MessageType.ERROR);

    private final String code;
    private final MessageType type;

    ApiErrorEnum(String code, MessageType type){
        this.code = code;
        this.type = type;
    }
}
