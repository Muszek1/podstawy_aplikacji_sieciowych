package pl.lodz.p.manager;

import org.springframework.stereotype.Service;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;

import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;


import java.util.List;

@Service
public class RoomManager {
    private final RoomRepositoryAdapterInterface adapter;

    public RoomManager(RoomRepositoryAdapterInterface repository) {
        this.adapter = repository;
    }

    public Room createRoom(Room room) {
        if (adapter.existsByNumber(room.getNumber())) {
            throw new UniqueValueException("Pokój o numerze " + room.getNumber() + " już istnieje");
        }

        return adapter.save(room);
    }

    public void updateRoom(Room externalRoom) {
        Room actualRoom = findRoomByNumber(externalRoom.getNumber());

        actualRoom.setBedsAmount(externalRoom.getBedsAmount());
        actualRoom.setBasePrice(externalRoom.getBasePrice());

        adapter.save(actualRoom);
    }


    public Room findRoomByNumber(String number) {
        return adapter.findByNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono pokoju o numerze: " + number));
    }

    public List<Room> findAllRooms() {
        return adapter.findAll();
    }

    public List<Room> findAvailableRooms() {
        return adapter.findByRented(false);
    }
}
