package br.ueg.acervodigitalarquitetura.exception;

import br.ueg.acervodigitalarquitetura.enums.ApiErrorEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ApiErrorEnum error;

    public BusinessException(String message){
        super(message);
        this.error = ApiErrorEnum.GENERAL;
    }
    public BusinessException(ApiErrorEnum err){
        super(err.getMessage());
        this.error = err;
    }
}
