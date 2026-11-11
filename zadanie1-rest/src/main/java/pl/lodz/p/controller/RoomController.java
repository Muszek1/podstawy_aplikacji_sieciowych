package pl.lodz.p.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.manager.RoomManager;
import pl.lodz.p.model.domain.Room;

import java.util.List;
import java.util.UUID;


@RestController
class RoomController {
    private final RoomManager roomManager;

    public RoomController(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @GetMapping("/rooms")
    public List<Room> getAll() {
        return roomManager.findAllRooms();
    }

    @GetMapping("/room/{number}")
    public Room getByNumber(@PathVariable String number) {
        return roomManager.findRoomByNumber(number);
    }

    @GetMapping("/rooms/{uuid}")
    public Room getByUuid(@PathVariable UUID uuid) {
        return roomManager.findRoomByUuid(uuid);
    }

    @PutMapping("/room/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRoom(@Valid @RequestBody Room updatedRoom, @PathVariable UUID uuid) {
        roomManager.updateRoom(updatedRoom, uuid);
    }

    @DeleteMapping("/room/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable UUID uuid) {
        roomManager.deleteRoom(uuid);
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public Room addRoom(@Valid @RequestBody Room newRoom) {
        return roomManager.createRoom(newRoom);
    }
}
