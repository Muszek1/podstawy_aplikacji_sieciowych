package pl.lodz.p.controller;


import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.manager.RentManager;
import pl.lodz.p.model.domain.Rent;

import java.time.LocalDateTime;
import java.util.List;


@Path("/rents")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class RentController {

    private final RentManager rentManager;

    public RentController(RentManager rentManager) {
        this.rentManager = rentManager;
    }

    @GET
    public List<Rent> getRents() {
        return rentManager.getAllRents();
    }

    @POST
    public Response createRent(
            @QueryParam("clientId") String clientId,
            @QueryParam("roomId") String roomId,
            @QueryParam("beginTime") LocalDateTime beginTime) {
        Rent rent = rentManager.createRent(clientId, roomId, beginTime);
        return Response.status(Response.Status.CREATED).entity(rent).build();
    }

    @PATCH
    @Path("/{id}/end")
    public void endRent(@PathParam("id") String id) {
        rentManager.endRent(id);
    }

    @GET
    @Path("/clients/{clientId}")
    public List<Rent> getClientRents(
            @PathParam("clientId") String clientId,
            @QueryParam("current") boolean current) {

        return rentManager.getClientRents(clientId, current);
    }

    @GET
    @Path("/rooms/{roomId}")
    public List<Rent> getRoomRents(
            @PathParam("roomId") String roomId,
            @QueryParam("current") boolean current) {

        return rentManager.getRoomRents(roomId, current);
    }

    @GET
    @Path("/{id}")
    public Rent getByUuid(@PathParam("id") String id) {
        return rentManager.findRentById(id);
    }

    @DELETE
    @Path("/{id}")
    public void deleteRent(@PathParam("id") String id) {
        rentManager.deleteRent(id);
    }
}
