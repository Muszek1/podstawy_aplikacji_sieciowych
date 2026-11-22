package pl.lodz.p.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;
import pl.lodz.p.manager.UserManager;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.ResourceAdministrator;
import pl.lodz.p.model.domain.User;
import pl.lodz.p.model.domain.UserAdministrator;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserController {

    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @GET
    public List<User> getAll(){
        return userManager.findAllUsers();
    }

    @GET
    @Path("/{id}")
    public User getByUuid(@PathParam("id") String id) {
        return userManager.findUserById(id);
    }

    @GET
    @Path("/by-login")
    public User getByLogin(@QueryParam("login") String login) {
        return userManager.findUserByLogin(login);
    }

    @GET
    @Path("/search")
    public List<User> search(@QueryParam("login") String login) {
        return userManager.findUsersByMatchingLogin(login);
    }

    @POST
    @Path("/clients")
    @ResponseStatus(201)
    public User addClient(
            @QueryParam("login") @NotBlank String login,
            @QueryParam("firstName") @NotBlank String firstName,
            @QueryParam("lastName") @NotBlank String lastName,
            @QueryParam("active") boolean active) {

        return userManager.createUser(
                Client.class, login, firstName, lastName, active
        );
    }

    @POST
    @Path("/resourceAdministrators")
    @ResponseStatus(201)
    public User addResourceAdministrator(
            @QueryParam("login") @NotBlank String login,
            @QueryParam("firstName") @NotBlank String firstName,
            @QueryParam("lastName") @NotBlank String lastName,
            @QueryParam("active") boolean active) {

        return userManager.createUser(
                ResourceAdministrator.class,
                login, firstName, lastName, active
        );
    }

    @POST
    @Path("/userAdministrators")
    @ResponseStatus(201)
    public User addUserAdministrator(
            @QueryParam("login") @NotBlank String login,
            @QueryParam("firstName") @NotBlank String firstName,
            @QueryParam("lastName") @NotBlank String lastName,
            @QueryParam("active") boolean active) {

        return userManager.createUser(
                UserAdministrator.class,
                login, firstName, lastName, active
        );
    }

    @PATCH
    @Path("/{id}/activate")
    public void activateUser(@PathParam("id") String id) {
        userManager.activateUser(id);
    }

    @PATCH
    @Path("/{id}/deactivate")
    public void deactivateUser(@PathParam("id") String id) {
        userManager.archiveUser(id);
    }

    @PUT
    @Path("/{id}")
    public void updateUser(
            @PathParam("id") String id,
            @QueryParam("firstName") @NotBlank String firstName,
            @QueryParam("lastName") @NotBlank String lastName,
            @HeaderParam("If-Match") Long version) {

        userManager.updateUser(id, firstName, lastName, version);
    }

}
