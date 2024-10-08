package br.ueg.acervodigitalarquitetura.exception;

import br.ueg.acervodigitalarquitetura.enums.ApiErrorEnum;
import lombok.Getter;

@Getter
public class ParameterRequiredException extends RuntimeException {
    private final ApiErrorEnum error;
    public ParameterRequiredException(String message){
        super(ApiErrorEnum.PARAMETER_REQUIRED.getMessage() + message);
        this.error = ApiErrorEnum.PARAMETER_REQUIRED;
    }
}
