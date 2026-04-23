package com.smartcampus.resources;

import com.smartcampus.models.SensorReading;
import com.smartcampus.services.ReadingService;
import com.smartcampus.store.InMemoryStore;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class ReadingsResource {
    private final String sensorId;
    private final ReadingService readingService = new ReadingService(InMemoryStore.getInstance());

    public ReadingsResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> listReadings() {
        return readingService.listReadings(sensorId);
    }

    @POST
    public Response createReading(SensorReading reading, @Context UriInfo uriInfo) {
        SensorReading created = readingService.addReading(sensorId, reading);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }
}
