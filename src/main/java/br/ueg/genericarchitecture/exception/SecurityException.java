package br.ueg.genericarchitecture.exception;

import br.ueg.genericarchitecture.enums.MessageCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

@Getter
public class SecurityException extends RuntimeException {
    private final MessageResponse messageResponse;

    public SecurityException(MessageResponse messageResponse){
        super();
        this.messageResponse = messageResponse;
    }

    public SecurityException(MessageCode error){
        super();
        messageResponse = new MessageResponse();
        messageResponse.setStatusCode(400);
        messageResponse.setMessages(new ArrayList<>());
        messageResponse.getMessages().add(new Message(error));
    }

    public SecurityException(MessageCode error, String... params){
        super();
        messageResponse = new MessageResponse();
        messageResponse.setStatusCode(400);
        messageResponse.setMessages(new ArrayList<>());
        messageResponse.getMessages().add(new Message(error));
    }

    public SecurityException(MessageCode error, HttpStatus status){
        super();
        messageResponse = new MessageResponse();
        messageResponse.setStatusCode(status.value());
        messageResponse.setMessages(new ArrayList<>());
        messageResponse.getMessages().add(new Message(error));
    }
}
