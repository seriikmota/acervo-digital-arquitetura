package br.ueg.acervodigitalarquitetura.exception;

import br.ueg.acervodigitalarquitetura.enums.ErrorEnum;
import lombok.Getter;

@Getter
public class ParameterRequiredException extends RuntimeException {
    private final ErrorEnum error;
    public ParameterRequiredException(String message){
        super(ErrorEnum.PARAMETER_REQUIRED.getMessage() + message);
        this.error = ErrorEnum.PARAMETER_REQUIRED;
    }
}
