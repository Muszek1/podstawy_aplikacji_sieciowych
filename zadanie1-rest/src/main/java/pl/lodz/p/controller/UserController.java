package pl.lodz.p.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.manager.UserManager;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.ResourceAdministrator;
import pl.lodz.p.model.domain.User;
import pl.lodz.p.model.domain.UserAdministrator;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping
    public List<User> getAll(){
        return userManager.findAllUsers();
    }

    @GetMapping("/{uuid}")
    public User getByUuid(@PathVariable UUID uuid){
        return userManager.findUserByUuid(uuid);
    }

    @GetMapping("/by-login")
    public User getByLogin(@RequestParam String login){
        return userManager.findUserByLogin(login);
    }

    @GetMapping("/search")
    public List<User> search(@RequestParam String login){
        return userManager.findUsersByMatchingLogin(login);
    }

    @PostMapping("/clients")
    @ResponseStatus(HttpStatus.CREATED)
    public User addClient(
            @RequestParam @NotBlank String login,
            @RequestParam @NotBlank String firstName,
            @RequestParam @NotBlank String lastName,
            @RequestParam boolean active) {

        return userManager.createUser(
                Client.class, login, firstName, lastName, active
        );
    }

    @PostMapping("/resourceAdministators")
    @ResponseStatus(HttpStatus.CREATED)
    public User addResourceAdministrator(
            @RequestParam @NotBlank String login,
            @RequestParam @NotBlank String firstName,
            @RequestParam @NotBlank String lastName,
            @RequestParam boolean active) {

        return userManager.createUser(
                ResourceAdministrator.class,
                login, firstName, lastName, active
        );
    }

    @PostMapping("/userAdministarators")
    @ResponseStatus(HttpStatus.CREATED)
    public User addUserAdministrator(
            @RequestParam @NotBlank String login,
            @RequestParam @NotBlank String firstName,
            @RequestParam @NotBlank String lastName,
            @RequestParam boolean active) {

        return userManager.createUser(
                UserAdministrator.class,
                login, firstName, lastName, active
        );
    }

    @PatchMapping("/{uuid}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateUser(@PathVariable UUID uuid) {
        userManager.activateUser(uuid);
    }

    @PatchMapping("/{uuid}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable UUID uuid) {
        userManager.archiveUser(uuid);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(
            @PathVariable UUID uuid,
            @RequestParam @NotBlank String firstName,
            @RequestParam @NotBlank String lastName) {

        userManager.updateUser(uuid, firstName, lastName);
    }

}
