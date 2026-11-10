package pl.lodz.p.manager;

import org.springframework.stereotype.Service;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;

import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        Room actualRoom = findRoomByUuid(externalRoom.getUuid());

        actualRoom.setBedsAmount(externalRoom.getBedsAmount());
        actualRoom.setBasePrice(externalRoom.getBasePrice());

        adapter.save(actualRoom);
    }


    public Room findRoomByNumber(String number) {
        return adapter.findByNumber(number);
                //.orElseThrow(() -> new EntityNotFoundException("Nie znaleziono pokoju o numerze: " + number));
    }

    public Room findRoomByUuid(UUID uuid) {
        return adapter.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono pokoju o UUID: " + uuid));
    }

    public List<Room> findAllRooms() {
        return adapter.findAll();
    }

    public void deleteRoom(String number) {
        try {
            Room room = adapter.findByNumber(number);
            adapter.delete(room);
        } catch (Exception e) {

        }
    }
}
