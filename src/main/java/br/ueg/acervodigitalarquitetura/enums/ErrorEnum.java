package br.ueg.acervodigitalarquitetura.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    GENERAL(400, "Erro desconhecido!"),
    NOT_FOUND(404, "Registro não encontrado!"),
    PARAMETER_REQUIRED(400, "Parâmetros obrigatório(s) não informados: ");

    private final Integer id;
    private final String message;

    ErrorEnum(Integer id, String message){
        this.id = id;
        this.message = message;
    }
}
