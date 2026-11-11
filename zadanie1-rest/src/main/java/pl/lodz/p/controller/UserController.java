package pl.lodz.p.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    public User addClient(@Valid @RequestBody Client client) {
        return userManager.createUser(client);
    }

    @PostMapping("/resourceAdministators")
    public User addResourceAdministrator(@Valid @RequestBody ResourceAdministrator resourceAdministrator) {
        return userManager.createUser(resourceAdministrator);
    }

    @PostMapping("/userAdministarators")
    public User addUserAdministrator(@Valid @RequestBody UserAdministrator userAdministrator) {
        return userManager.createUser(userAdministrator);
    }

    @PatchMapping("/{uuid}/activate")
    public void activateUser(@PathVariable UUID uuid) {
        userManager.activateUser(uuid);
    }

    @PatchMapping("/{uuid}/deactivate")
    public void deactivateUser(@PathVariable UUID uuid) {
        userManager.archiveUser(uuid);
    }

    @PutMapping("/{uuid}")
    public User updateUser(
            @PathVariable UUID uuid,
            @RequestParam @NotBlank String firstName,
            @RequestParam @NotBlank String lastName) {

        return userManager.updateUser(uuid, firstName, lastName);
    }

}
