package pl.lodz.p.controller;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.manager.RentManager;
import pl.lodz.p.model.domain.Rent;

import java.time.LocalDateTime;
import java.util.List;

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
    public Rent createRent(@RequestParam String clientId,
                           @RequestParam String roomId,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                           LocalDateTime beginTime) {

        return rentManager.createRent(clientId, roomId, beginTime);
    }

    @PatchMapping("/{id}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void endRent(@PathVariable String id) {
        rentManager.endRent(id);
    }

    @GetMapping("/clients/{clientId}")
    public List<Rent> getClientRents(
            @PathVariable String clientId,
            @RequestParam boolean current) {

        return rentManager.getClientRents(clientId, current);
    }

    @GetMapping("/rooms/{roomId}")
    public List<Rent> getRoomRents(
            @PathVariable String roomId,
            @RequestParam boolean current) {

        return rentManager.getRoomRents(roomId, current);
    }

    @GetMapping("/{id}")
    public Rent getByUuid(@PathVariable String id) {
        return rentManager.findRentById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRent(@PathVariable String id) {
        rentManager.deleteRent(id);
    }
}
