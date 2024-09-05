package br.ueg.acervodigitalarquitetura.validation;

import br.ueg.acervodigitalarquitetura.enums.ValidationActionsEnum;

public interface IValidations<MODEL> {
    void validate(MODEL data, ValidationActionsEnum action);
}
