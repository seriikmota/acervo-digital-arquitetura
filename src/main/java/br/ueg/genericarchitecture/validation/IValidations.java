package br.ueg.genericarchitecture.validation;

import br.ueg.genericarchitecture.enums.ValidationActionsEnum;
import br.ueg.genericarchitecture.exception.Message;

import java.util.List;

public interface IValidations<MODEL> {
    void validate(MODEL data, ValidationActionsEnum action, List<Message> messagesToThrow);
}
