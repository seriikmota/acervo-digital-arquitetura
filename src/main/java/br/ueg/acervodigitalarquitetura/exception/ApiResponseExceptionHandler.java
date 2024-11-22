package br.ueg.acervodigitalarquitetura.exception;

import br.ueg.acervodigitalarquitetura.enums.MessageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;

public abstract class ApiResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(DataException.class)
    public ResponseEntity<MessageResponse> handleDataException(DataException ex){
        mountMessages(ex.getMessageResponse());

        return ResponseEntity.status(ex.getMessageResponse().getStatusCode()).body(ex.getMessageResponse());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<MessageResponse> handleBusinessException(BusinessException ex) {
        mountMessages(ex.getMessageResponse());

        return ResponseEntity.status(ex.getMessageResponse().getStatusCode()).body(ex.getMessageResponse());
    }


    protected MessageResponse mountMessageResponse(HttpStatus status, MessageCode messageCode) {
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setStatusCode(status.value());
        messageResponse.setMessages(new ArrayList<>());
        messageResponse.getMessages().add(new Message(messageCode));

        mountMessages(messageResponse);

        return messageResponse;
    }


    protected void mountMessages(MessageResponse messageResponse) {
        if (messageResponse != null && messageResponse.getMessages() != null) {
            for (Message message : messageResponse.getMessages()) {
                if (message.getParams() == null || message.getParams().length == 0)
                    message.setMessage(getMessage(message.getCode()));
                else
                    message.setMessage(getMessage(message.getCode(), message.getParams()));
            }
        }
    }

    private String getMessage(final String code, final Object... params) {
        return messageSource.getMessage(code, params, LocaleContextHolder.getLocale());
    }
}
