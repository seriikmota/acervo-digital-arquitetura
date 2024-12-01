package br.ueg.genericarchitecture.exception;

import br.ueg.genericarchitecture.enums.ApiErrorEnum;
import lombok.Getter;

@Getter
public class ParameterRequiredException extends RuntimeException {
    private final ApiErrorEnum error;
    public ParameterRequiredException(String message){
        super(ApiErrorEnum.PARAMETER_REQUIRED.getCode() + message);
        this.error = ApiErrorEnum.PARAMETER_REQUIRED;
    }
}
