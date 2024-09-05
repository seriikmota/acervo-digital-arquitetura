package br.ueg.acervodigitalarquitetura.exception;

import br.ueg.acervodigitalarquitetura.enums.ErrorEnum;
import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {
    private final ErrorEnum error;

    public BusinessLogicException(String message){
        super(message);
        this.error = ErrorEnum.GENERAL;
    }
    public BusinessLogicException(ErrorEnum err){
        super(err.getMessage());
        this.error = err;
    }
}
