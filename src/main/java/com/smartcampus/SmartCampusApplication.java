package com.smartcampus;

import com.smartcampus.filters.RequestResponseLoggingFilter;
import com.smartcampus.mappers.ConflictExceptionMapper;
import com.smartcampus.mappers.ForbiddenOperationExceptionMapper;
import com.smartcampus.mappers.GlobalExceptionMapper;
import com.smartcampus.mappers.JaxRsNotFoundExceptionMapper;
import com.smartcampus.mappers.ResourceNotFoundExceptionMapper;
import com.smartcampus.mappers.UnprocessableEntityExceptionMapper;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        packages("com.smartcampus");
        register(org.glassfish.jersey.jackson.JacksonFeature.class);
        register(RequestResponseLoggingFilter.class);
        register(ConflictExceptionMapper.class);
        register(UnprocessableEntityExceptionMapper.class);
        register(ForbiddenOperationExceptionMapper.class);
        register(ResourceNotFoundExceptionMapper.class);
        register(JaxRsNotFoundExceptionMapper.class);
        register(GlobalExceptionMapper.class);
    }
}
