package pl.lodz.p.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.manager.UserManager;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.ResourceAdministrator;
import pl.lodz.p.model.domain.User;
import pl.lodz.p.model.domain.UserAdministrator;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequestScope
public class UserController {

    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping
    public List<User> getAll(){
        return userManager.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getByUuid(@PathVariable String id){
        return userManager.findUserById(id);
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

    @PostMapping("/resourceAdministrators")
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

    @PostMapping("/userAdministrators")
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

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateUser(@PathVariable String id) {
        userManager.activateUser(id);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable String id) {
        userManager.archiveUser(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(
            @PathVariable String id,
            @RequestParam @NotBlank String firstName,
            @RequestParam @NotBlank String lastName,
            @RequestHeader("If-Match") Long version) {

        userManager.updateUser(id, firstName, lastName, version);
    }

}
