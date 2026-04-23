package com.smartcampus.mappers;

import com.smartcampus.api.ApiError;
import com.smartcampus.exceptions.UnprocessableEntityException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;

@Provider
public final class UnprocessableEntityExceptionMapper implements ExceptionMapper<UnprocessableEntityException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(UnprocessableEntityException exception) {
        Response.Status status = Response.Status.fromStatusCode(422);
        ApiError error = new ApiError(
                422,
                status == null ? "Unprocessable Entity" : status.getReasonPhrase(),
                exception.getMessage(),
                uriInfo == null ? null : uriInfo.getPath(),
                Instant.now()
        );
        return Response.status(422).type(MediaType.APPLICATION_JSON_TYPE).entity(error).build();
    }
}
