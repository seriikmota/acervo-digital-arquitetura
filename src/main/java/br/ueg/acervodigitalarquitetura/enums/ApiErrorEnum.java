package br.ueg.acervodigitalarquitetura.enums;

import lombok.Getter;

@Getter
public enum ApiErrorEnum {
    GENERAL(400, "Erro desconhecido!"),
    NOT_FOUND(404, "Registro não encontrado!"),
    PARAMETER_REQUIRED(400, "Parâmetros obrigatório(s) não informados: "),
    LOGIN_INVALID(400, "Login e/ou senha inválido(s)!"),
    USER_PASSWORD_NOT_MATCH(400, "Login e/ou senha incorreto(s)!"),
    INACTIVE_USER(400, "Usuário inativo!"),
    INVALID_TOKEN(400, "Token inválido!"),
    EXPIRED_TOKEN(400, "Token expirado!");

    private final Integer id;
    private final String message;

    ApiErrorEnum(Integer id, String message){
        this.id = id;
        this.message = message;
    }
}
