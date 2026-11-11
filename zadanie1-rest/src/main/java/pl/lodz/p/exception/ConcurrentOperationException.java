package pl.lodz.p.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConcurrentOperationException extends RepositoryException {
    public ConcurrentOperationException(String message) {
        super(message);
    }


}
