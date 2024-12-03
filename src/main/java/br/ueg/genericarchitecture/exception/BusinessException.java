package br.ueg.genericarchitecture.exception;

import br.ueg.genericarchitecture.enums.MessageCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

@Getter
public class BusinessException extends RuntimeException {
    private final MessageResponse messageResponse;

    public BusinessException(MessageResponse messageResponse){
        super();
        this.messageResponse = messageResponse;
    }

    public BusinessException(MessageCode error){
        super();
        messageResponse = new MessageResponse();
        messageResponse.setStatusCode(400);
        messageResponse.setMessages(new ArrayList<>());
        messageResponse.getMessages().add(new Message(error));
    }

    public BusinessException(MessageCode error, String... params){
        super();
        messageResponse = new MessageResponse();
        messageResponse.setStatusCode(400);
        messageResponse.setMessages(new ArrayList<>());
        messageResponse.getMessages().add(new Message(error, params));
    }

    public BusinessException(MessageCode error, HttpStatus status){
        super();
        messageResponse = new MessageResponse();
        messageResponse.setStatusCode(status.value());
        messageResponse.setMessages(new ArrayList<>());
        messageResponse.getMessages().add(new Message(error));
    }
}
