package com.smartcampus.mappers;

import com.smartcampus.api.ApiError;
import com.smartcampus.exceptions.ForbiddenOperationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;

@Provider
public final class ForbiddenOperationExceptionMapper implements ExceptionMapper<ForbiddenOperationException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ForbiddenOperationException exception) {
        ApiError error = new ApiError(
                Response.Status.FORBIDDEN.getStatusCode(),
                Response.Status.FORBIDDEN.getReasonPhrase(),
                exception.getMessage(),
                uriInfo == null ? null : uriInfo.getPath(),
                Instant.now()
        );
        return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON_TYPE).entity(error).build();
    }
}
