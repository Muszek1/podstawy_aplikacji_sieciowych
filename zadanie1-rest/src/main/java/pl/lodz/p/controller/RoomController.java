package pl.lodz.p.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.manager.RoomManager;
import pl.lodz.p.model.domain.Room;

import java.util.List;


@RestController
@RequestScope
class RoomController {
    private final RoomManager roomManager;

    public RoomController(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @GetMapping("/rooms")
    public List<Room> getAll() {
        return roomManager.findAllRooms();
    }

    @GetMapping("/rooms/number/{number}")
    public Room getByNumber(@PathVariable String number) {
        return roomManager.findRoomByNumber(number);
    }

    @GetMapping("/rooms/{id}")
    public Room getById(@PathVariable String id) {
        return roomManager.findRoomById(id);
    }

    @PutMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRoom(
            @PathVariable String id,
            @RequestParam @Positive int bedsAmount,
            @RequestParam @PositiveOrZero double price,
            @RequestHeader("If-Match") Long version) {

        roomManager.updateRoom(id, bedsAmount, price, version);
    }

    @DeleteMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable String id) {
        roomManager.deleteRoom(id);
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public Room addRoom(
            @RequestParam @NotBlank String number,
            @RequestParam @Positive int bedsAmount,
            @RequestParam @PositiveOrZero double price) {

        return roomManager.createRoom(number, bedsAmount, price);
    }
}
