package pl.lodz.p.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.manager.RoomManager;
import pl.lodz.p.model.db.RoomEnt;

import java.util.List;


@RestController
class RoomController {
    private final RoomManager roomManager;

    public RoomController(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @GetMapping("/rooms")
    public List<RoomEnt> getAll() {
        return roomManager.findAllRooms();
    }

    @GetMapping("/room/{number}")
    public RoomEnt getByNumber(@PathVariable String number) {
        return roomManager.findRoomByNumber(number);
    }

    @PutMapping("/room/{number}")
    public RoomEnt updateRoom(@RequestBody RoomEnt updatedRoom, @PathVariable String number) {
        return null;
    }

    @DeleteMapping("/room/{number}")
    public void deleteRoom(@PathVariable String number) {
        roomManager.deleteRoom(number);
    }
}
