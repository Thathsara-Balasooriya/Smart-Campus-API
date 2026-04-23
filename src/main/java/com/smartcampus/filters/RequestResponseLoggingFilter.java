package com.smartcampus.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public final class RequestResponseLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger logger = Logger.getLogger(RequestResponseLoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = String.valueOf(requestContext.getUriInfo().getRequestUri());
        logger.log(Level.INFO, "REQUEST {0} {1}", new Object[]{method, uri});
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = String.valueOf(requestContext.getUriInfo().getRequestUri());
        int status = responseContext.getStatus();
        logger.log(Level.INFO, "RESPONSE {0} {1} -> {2}", new Object[]{method, uri, status});
    }
}
