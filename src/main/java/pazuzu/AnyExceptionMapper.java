package pazuzu;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.server.ParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pazuzu.web.dto.ErrorDto;

public class AnyExceptionMapper implements ExceptionMapper<ParamException> {
    private static final Logger LOG = LoggerFactory.getLogger(UnknownExceptionMapper.class);

    @Override
    public Response toResponse(ParamException e) {
        LOG.error("Something extremely bad has happen", e);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDto("bad_request", "Failed to process request. Probably it's time to see logs"))
                .build();
    }

}
