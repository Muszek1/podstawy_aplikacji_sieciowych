package pl.lodz.p.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        int status;
        if (exception instanceof EntityNotFoundException) {
            status = 404;
        } else if (exception instanceof InvalidValueException) {
            status = 400;
        } else if (exception instanceof BusinessLogicException
                || exception instanceof ConcurrentOperationException) {
            status = 409;
        } else {
            status = 500;
        }
        return Response.status(status)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}