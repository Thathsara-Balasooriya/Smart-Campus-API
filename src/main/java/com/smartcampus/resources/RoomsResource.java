package com.smartcampus.resources;

import com.smartcampus.models.Room;
import com.smartcampus.services.RoomService;
import com.smartcampus.store.InMemoryStore;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class RoomsResource {
    private final RoomService roomService = new RoomService(InMemoryStore.getInstance());

    @GET
    public List<Room> listRooms() {
        return roomService.listRooms();
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        Room created = roomService.createRoom(room);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("{id}")
    public Room getRoom(@PathParam("id") String id) {
        return roomService.getRoomOrThrow(id);
    }

    @PUT
    @Path("{id}")
    public Room updateRoom(@PathParam("id") String id, Room room) {
        return roomService.updateRoom(id, room);
    }

    @DELETE
    @Path("{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        roomService.deleteRoom(id);
        return Response.noContent().build();
    }
}
