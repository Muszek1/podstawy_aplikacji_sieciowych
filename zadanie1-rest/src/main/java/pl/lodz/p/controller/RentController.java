package pl.lodz.p.controller;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.manager.RentManager;
import pl.lodz.p.model.domain.Rent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rents")
@RequestScope
public class RentController {

    private final RentManager rentManager;

    public RentController(RentManager rentManager) {
        this.rentManager = rentManager;
    }

    @GetMapping
    public List<Rent> getRents() {
        return rentManager.getAllRents();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rent createRent(@RequestParam UUID clientUUID,
                           @RequestParam UUID roomUUID,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                           LocalDateTime beginTime) {

        return rentManager.createRent(clientUUID, roomUUID, beginTime);
    }

    @PatchMapping("/{uuid}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void endRent(@PathVariable UUID uuid) {
        rentManager.endRent(uuid);
    }

    @GetMapping("/clients/{clientUuid}")
    public List<Rent> getClientRents(
            @PathVariable UUID clientUuid,
            @RequestParam boolean current) {

        return rentManager.getClientRents(clientUuid, current);
    }

    @GetMapping("/rooms/{roomUuid}")
    public List<Rent> getRoomRents(
            @PathVariable UUID roomUuid,
            @RequestParam boolean current) {

        return rentManager.getRoomRents(roomUuid, current);
    }

    @GetMapping("/{uuid}")
    public Rent getByUuid(@PathVariable UUID uuid) {
        return rentManager.findRentByUuid(uuid);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRent(@PathVariable UUID uuid) {
        rentManager.deleteRent(uuid);
    }
}
