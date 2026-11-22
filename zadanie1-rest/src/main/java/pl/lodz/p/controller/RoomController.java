package pl.lodz.p.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.ResponseStatus;
import pl.lodz.p.manager.RoomManager;
import pl.lodz.p.model.domain.Room;

import java.util.List;


@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
class RoomController {
    private final RoomManager roomManager;

    public RoomController(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @GET
    public List<Room> getAll() {
        return roomManager.findAllRooms();
    }

    @GET
    @Path("/number/{number}")
    public Room getByNumber(@PathParam("number") String number) {
        return roomManager.findRoomByNumber(number);
    }

    @GET
    @Path("/{id}")
    public Room getById(@PathParam("id") String id) {
        return roomManager.findRoomById(id);
    }

    @PUT
    @Path("/{id}")
    public void updateRoom(
            @PathParam("id") String id,
            @QueryParam("bedsAmount") @Positive int bedsAmount,
            @QueryParam("price") @PositiveOrZero double price,
            @HeaderParam("If-Match") Long version) {
        roomManager.updateRoom(id, bedsAmount, price, version);
    }

    @DELETE
    @Path("/{id}")
    public void deleteRoom(@PathParam("id") String id) {
        roomManager.deleteRoom(id);
    }
    @POST
    public Response addRoom(
            @QueryParam("number") @NotBlank String number,
            @QueryParam("bedsAmount") @Positive int bedsAmount,
            @QueryParam("price") @PositiveOrZero double price) {
        Room room = roomManager.createRoom(number, bedsAmount, price);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }
}
