package br.ueg.acervodigitalarquitetura.exception;

import br.ueg.acervodigitalarquitetura.enums.ApiErrorEnum;
import lombok.Getter;

@Getter
public class DataException extends RuntimeException {
    private final ApiErrorEnum error;

    public DataException(String message){
        super(message);
        this.error = ApiErrorEnum.GENERAL;
    }
    public DataException(ApiErrorEnum err){
        super(err.getMessage());
        this.error = err;
    }
}
