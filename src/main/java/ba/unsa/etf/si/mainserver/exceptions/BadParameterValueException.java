package ba.unsa.etf.si.mainserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BadParameterValueException extends RuntimeException{
    public BadParameterValueException() {
    }

    public BadParameterValueException(String message) {
        super(message);
    }
}
