package br.ueg.acervodigitalarquitetura.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    GENERAL(400, "Erro desconhecido!"),
    NOT_FOUND(404, "Registro não encontrado!"),
    PARAMETER_REQUIRED(400, "Parâmetros obrigatório(s) não informados: "),
    CONFIRM_PASSWORD_NOT_ENTIRED(400, "Confirmação de senha não informada!"),
    PASSWORD_NOT_ENTIRED(400, "Senha não informada!"),
    PASSWORD_DIFERENT(400, "Ambas senhas estão diferentes!"),
    PASSWORD_INVALID(400, "Senha inválida!");

    private final Integer id;
    private final String message;

    ErrorEnum(Integer id, String message){
        this.id = id;
        this.message = message;
    }
}
