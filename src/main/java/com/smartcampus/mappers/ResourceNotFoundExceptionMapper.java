package com.smartcampus.mappers;

import com.smartcampus.api.ApiError;
import com.smartcampus.exceptions.ResourceNotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;

@Provider
public final class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        ApiError error = new ApiError(
                Response.Status.NOT_FOUND.getStatusCode(),
                Response.Status.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                uriInfo == null ? null : uriInfo.getPath(),
                Instant.now()
        );
        return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(error).build();
    }
}
