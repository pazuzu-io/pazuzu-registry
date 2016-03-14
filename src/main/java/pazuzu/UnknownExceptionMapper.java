package pazuzu;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pazuzu.web.dto.ErrorDto;


public class UnknownExceptionMapper implements ExceptionMapper<RuntimeException> {
    private static final Logger LOG = LoggerFactory.getLogger(UnknownExceptionMapper.class);
    @Override
    public Response toResponse(RuntimeException e) {
        LOG.error("Unexpected exception occurred", e);
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorDto("unknown", "See log for details"))
                .build();
    }
}
