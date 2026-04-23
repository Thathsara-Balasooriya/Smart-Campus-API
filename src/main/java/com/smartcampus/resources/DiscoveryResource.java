package com.smartcampus.resources;

import com.smartcampus.api.ApiLink;
import com.smartcampus.api.DiscoveryResponse;
import com.smartcampus.models.Sensor;
import com.smartcampus.services.SensorService;
import com.smartcampus.store.InMemoryStore;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Instant;
import java.util.List;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public final class DiscoveryResource {
    private final SensorService sensorService = new SensorService(InMemoryStore.getInstance());

    @GET
    public DiscoveryResponse getDiscovery(@Context UriInfo uriInfo) {
        URI base = uriInfo.getBaseUri();

        List<ApiLink> links = List.of(
                new ApiLink("self", base.toString(), "GET"),
                new ApiLink("rooms", base.resolve("rooms").toString(), "GET"),
                new ApiLink("create-room", base.resolve("rooms").toString(), "POST"),
                new ApiLink("sensors", base.resolve("sensors").toString(), "GET"),
                new ApiLink("create-sensor", base.resolve("sensors").toString(), "POST")
        );

        return new DiscoveryResponse("Smart Campus API", "v1", Instant.now(), links);
    }

    @GET
    @Path("{id}")
    public Sensor getSensor(@PathParam("id") String id) {
        return sensorService.getSensorOrThrow(id);
    }

    @Path("{id}/read")
    public ReadingsResource readings(@PathParam("id") String sensorId) {
        return new ReadingsResource(sensorId);
    }
}
