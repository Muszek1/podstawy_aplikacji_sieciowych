package pl.lodz.p.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.manager.RoomManager;
import pl.lodz.p.model.domain.Room;

import java.util.List;


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

    @PutMapping("/room/{number}")
    public void updateRoom(@RequestBody Room updatedRoom, @PathVariable String number) {
        roomManager.updateRoom(updatedRoom);
    }

    @DeleteMapping("/room/{number}")
    public void deleteRoom(@PathVariable String number) {
        roomManager.deleteRoom(number);
    }

    @PostMapping("/rooms")
    public Room addRoom(@RequestBody Room newRoom) {
        return roomManager.createRoom(newRoom);
    }
}
