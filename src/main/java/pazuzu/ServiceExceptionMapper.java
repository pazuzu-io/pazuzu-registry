package pazuzu;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import pazuzu.service.ServiceException;
import pazuzu.web.dto.ErrorDto;

public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {
    @Override
    public Response toResponse(ServiceException e) {
        return Response.status(getHttpCode(e)).entity(new ErrorDto(e.getCode(), e.getMessage())).build();
    }

    private Response.Status getHttpCode(ServiceException e) {
        if (e instanceof ServiceException.NotFoundException) {
            return Response.Status.NOT_FOUND;
        } else {
            return Response.Status.BAD_REQUEST;
        }
    }
}
