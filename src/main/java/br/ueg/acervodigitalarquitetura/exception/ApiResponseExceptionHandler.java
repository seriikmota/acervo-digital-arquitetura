package br.ueg.acervodigitalarquitetura.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public abstract class ApiResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataException.class)
    public ResponseEntity<String> handleDataException(DataException ex){
        return ResponseEntity.status(ex.getError().getId()).body(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getError().getId()).body(ex.getMessage());
    }
}
