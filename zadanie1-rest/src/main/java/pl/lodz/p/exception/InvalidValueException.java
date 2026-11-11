package pl.lodz.p.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidValueException extends BusinessLogicException {
    public InvalidValueException(String message) {
        super(message);
    }
}
